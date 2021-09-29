package com.yxl.smmall.wares.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxl.common.constant.OrderStatusEnum;
import com.yxl.common.to.mq.SrockLockedTo;
import com.yxl.common.to.mq.StockDetailTo;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.Query;
import com.yxl.common.utils.R;
import com.yxl.common.vo.OrderItemVo;
import com.yxl.common.vo.OrderVo;
import com.yxl.common.vo.SkuHasStockVO;
import com.yxl.common.vo.WareSkuLockVo;
import com.yxl.smmall.wares.dao.WmsWareSkuDao;
import com.yxl.smmall.wares.entity.WmsWareOrderTaskDetailEntity;
import com.yxl.smmall.wares.entity.WmsWareOrderTaskEntity;
import com.yxl.smmall.wares.entity.WmsWareSkuEntity;
import com.yxl.smmall.wares.excrption.NoStockException;
import com.yxl.smmall.wares.feign.OrderFegin;
import com.yxl.smmall.wares.feign.ProductFegin;
import com.yxl.smmall.wares.service.WmsWareOrderTaskDetailService;
import com.yxl.smmall.wares.service.WmsWareOrderTaskService;
import com.yxl.smmall.wares.service.WmsWareSkuService;
import lombok.Data;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO:库存解锁的场景：
 * 1，下订单成功，订单过期没有支付系统自动取消，被用户手动取消，都要解锁库存
 * 2，下订单成功，库存锁定成功，接下来的业务调用失败，导致订单回滚，之前锁定的库存就要自动解锁
 */

@Service("wmsWareSkuService")
public class WmsWareSkuServiceImpl extends ServiceImpl<WmsWareSkuDao, WmsWareSkuEntity> implements WmsWareSkuService {
    @Autowired
    WmsWareSkuDao wmsWareSkuDao;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    ProductFegin productFegin;
    @Autowired
    WmsWareOrderTaskService wmsWareOrderTaskService;
    @Autowired
    WmsWareOrderTaskDetailService wmsWareOrderTaskDetailService;
    @Autowired
    OrderFegin orderFegin;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WmsWareSkuEntity> page = this.page(
                new Query<WmsWareSkuEntity>().getPage(params),
                new QueryWrapper<WmsWareSkuEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 场景：
     * 1,库存自动解锁，下订单成功，库存锁定成功，接下来的业务调用失败，导致订单回滚，之前锁定的库存就要自动解锁
     * 2，订单失败，锁库存失败
     *
     */

    private void unlockedStock(Long skuId, Long wareId, Integer lockNumber, Long taskDetail) {
        //库存解锁
        wmsWareSkuDao.unlockStock(skuId, wareId,lockNumber);
        //更新库存工作单的状态
        WmsWareOrderTaskDetailEntity entity = new WmsWareOrderTaskDetailEntity();
        entity.setId(taskDetail);
        entity.setLockStatus(2);
        wmsWareOrderTaskDetailService.updateById(entity);
    }


    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<WmsWareSkuEntity> wrapper = new QueryWrapper<>();

        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)) {
            wrapper.eq("sku_id", skuId);
        }

        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)) {
            wrapper.eq("ware_id", wareId);
        }


        IPage<WmsWareSkuEntity> page = this.page(
                new Query<WmsWareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);

    }

    @Override
    public List<SkuHasStockVO> getSkusHasStock(List<Long> skuIdList) {
        //查询当前的sku的库存重量
//      SELECT SUM(stock-stock_locked) FROM wms_ware_sku WHERE sku_id=3
        List<SkuHasStockVO> collect = skuIdList.stream().map(item -> {
            SkuHasStockVO skuHasStockVO = new SkuHasStockVO();
            Long counter = baseMapper.getSkuStock(item);
            skuHasStockVO.setSku_id(item);
            skuHasStockVO.setHasstock(counter != null && counter > 0);
            return skuHasStockVO;
        }).collect(Collectors.toList());

        return collect;
    }

    /**
     *
     * 锁库存
     * 库存解锁的场景：
     * 1，下订单那成功，订单过期没有支付被系统自动取消，被用户手动取消，都要解锁库存
     * 2.下订单成功，所库存成功，业务失败，导致订单回滚 之前锁定的库存解锁
     *
     * @param vo
     * @return
     */
    @Transactional(rollbackFor = NoStockException.class)
    //默认是异常就会回滚
    @Override
    public Boolean orderlockstock(WareSkuLockVo vo) {
        /**
         * 保存库存工作单的详情
         * 追溯
         */
        WmsWareOrderTaskEntity entity = new WmsWareOrderTaskEntity();
        entity.setOrderSn(vo.getOrderSn());
        wmsWareOrderTaskService.save(entity);

        //1,按照下单的收货地址，找到一个就近仓库，锁定库存
        //1，找到每一个商品在那个 仓库都有库存
        List<OrderItemVo> locks = vo.getLocks();
        List<SkuWareHasItem> collect = locks.stream().map(orderItemVo -> {
            SkuWareHasItem skuWareHasItem = new SkuWareHasItem();
            Long skuId = orderItemVo.getSkuId();
            skuWareHasItem.setSkuId(skuId);
            Integer count = orderItemVo.getCount();
            skuWareHasItem.setLockNum(count);
            //查询这个商品在哪里有库存
            List<Long> wareIds = wmsWareSkuDao.selectWareId(skuId);
            skuWareHasItem.setWareIds(wareIds);
            return skuWareHasItem;
        }).collect(Collectors.toList());

        //TODO:2，锁定库存
        for (SkuWareHasItem skuWareHasItem : collect) {
            Boolean skuStocked = false;
            Integer lockNum = skuWareHasItem.getLockNum();
            Long skuId = skuWareHasItem.getSkuId();
            List<Long> wareIds = skuWareHasItem.getWareIds();
            if (wareIds == null || wareIds.size() == 0) {
                //没有任何仓库有该商品的库存
                throw new NoStockException(skuId);
            }
            //每一个商品都锁定成功，将当前商品锁定了几件的工作单记录发送个MQ
            //锁定失败，前面你保存工作单信息的操作会执行回滚操作，即使解锁记录，由于数据库查不到ID，所以就不用回滚
            for (Long wareId : wareIds) {
                //是否锁定成功通过判断多少行受影响
                Long count = wmsWareSkuDao.lockSkuStock(skuId, wareId, lockNum);
                if (count == 1) {
                    //表示加锁成功,跳出循环，锁住一个仓库即可
                    skuStocked = true;
                    //TODO:将锁库存的消息发送给Rabbit MQ消息中间件
                    WmsWareOrderTaskDetailEntity wmsWareOrderTaskDetailEntity = new WmsWareOrderTaskDetailEntity(null, skuId, "", lockNum, entity.getId(), wareId, 1);
                    wmsWareOrderTaskDetailService.save(wmsWareOrderTaskDetailEntity);
                    SrockLockedTo stockLockedTO = new SrockLockedTo();
                    StockDetailTo stockDetailTO = new StockDetailTo();
                    BeanUtils.copyProperties(wmsWareOrderTaskDetailEntity, stockDetailTO);
                    stockLockedTO.setId(entity.getId());
                    stockLockedTO.setDetail(stockDetailTO);
                    rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", stockLockedTO);
                    break;
                }
                    //表示锁定库存失败 重试下一个库存
            }
            if (skuStocked == false) {
                //当所有库存都锁定失败，表示该商品在所有库存中没有库存了；
                throw new NoStockException(skuId);
            }

        }
        return true;
    }

    @Override
    public void unlockStock(SrockLockedTo to) {

        System.out.println("收到解锁库存的消息");
        Long id = to.getId();
        StockDetailTo detail = to.getDetail();
        Long detailId = detail.getId();
        //解锁操作：
//        1，查询数据库关于这个订单的锁定库存信息
//            有：证明库存锁定成功了
//                1，没有这个订单，必须解锁库存
//                 2，有这个订单，不是解锁库存
//                    订单状态：已取消，及所库存
//                      没有取消，不能解锁
//            没有：库存锁定失败了，库存回滚了，这种情况无需解锁；
        WmsWareOrderTaskDetailEntity byId = wmsWareOrderTaskDetailService.getById(detailId);
        if (byId != null) {
            //现需要解锁
            Long toId = to.getId();
            WmsWareOrderTaskEntity taskServiceById = wmsWareOrderTaskService.getById(toId);
            String orderSn = taskServiceById.getOrderSn();
//           TODO:这里有一个隐藏的错误，由于Order服务设置了拦截器，将该路径进行了拦截，验证失败后跳转到登陆页面，在拦截器中对这个路径放行
            R r = orderFegin.getOrderStatus(orderSn);
            if (r.getCode() == 0) {
                OrderVo data = r.getData(new TypeReference<OrderVo>() {
                });

                if (data == null || data.getStatus() == OrderStatusEnum.CANCLED.getCode()) {
                    //订单应尽被取消，才能解锁库存
                    if (byId.getLockStatus() == 1){
                        unlockedStock(byId.getSkuId(), byId.getWareId(), byId.getSkuNum(), toId);
                    }

                } else {
                    //消息拒绝以后重新放在队列里面，让别人继续消费解锁
                    throw new RuntimeException("远程服务失败");
                }
            }
        } else {
            //无需解锁
        }


    }

    /**
     * 防止订单服务卡顿，导致订单状态消息一直改不了，库存消息由先到达，查订单状态新建，什么都不做就过来
     * 导致卡顿的订单永远也解不了锁
     * @param orderVo
     */
    @Override
    public void unlockStock(OrderVo orderVo) {
        String orderSn = orderVo.getOrderSn();
        //为了防止重复解锁库存，先查一下库存的状态
       WmsWareOrderTaskEntity entity =  wmsWareOrderTaskService.getOrderTaskByOrderSn(orderSn);
        Long id = entity.getId();
        List<WmsWareOrderTaskDetailEntity> list = wmsWareOrderTaskDetailService.list(new QueryWrapper<WmsWareOrderTaskDetailEntity>().eq("task_id", id).eq("lock_status", 1));
        for (WmsWareOrderTaskDetailEntity wmsWareOrderTaskDetailEntity : list) {
            unlockedStock(wmsWareOrderTaskDetailEntity.getSkuId(),wmsWareOrderTaskDetailEntity.getWareId(),wmsWareOrderTaskDetailEntity.getSkuNum(),wmsWareOrderTaskDetailEntity.getId());

        }
    }

}

    @Data
    class SkuWareHasItem {
        private Long skuId;
        private Integer lockNum;
        private List<Long> wareIds;
    }

