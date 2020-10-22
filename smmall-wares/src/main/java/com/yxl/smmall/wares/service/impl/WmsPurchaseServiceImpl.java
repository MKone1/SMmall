package com.yxl.smmall.wares.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxl.common.constant.WaresConstant;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.Query;
import com.yxl.common.vo.MergeVO;
import com.yxl.smmall.wares.dao.WmsPurchaseDao;
import com.yxl.smmall.wares.entity.WmsPurchaseDetailEntity;
import com.yxl.smmall.wares.entity.WmsPurchaseEntity;
import com.yxl.smmall.wares.service.WmsPurchaseDetailService;
import com.yxl.smmall.wares.service.WmsPurchaseService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("wmsPurchaseService")
public class WmsPurchaseServiceImpl extends ServiceImpl<WmsPurchaseDao, WmsPurchaseEntity> implements WmsPurchaseService {
    @Autowired
    WmsPurchaseDetailService purchaseDetailService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WmsPurchaseEntity> wrapper = new QueryWrapper<>();
        /**
         * key:
         * status: 2
         */

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((w) -> {
                w.eq("id", key).or().eq("assignee_id", key).or().like("assignee_name", key);
            });
        }
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            wrapper.eq("status", status);
        }
        IPage<WmsPurchaseEntity> page = this.page(
                new Query<WmsPurchaseEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 查询没有被领取的采购需求
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageUnReceive(Map<String, Object> params) {

        IPage<WmsPurchaseEntity> page = this.page(
                new Query<WmsPurchaseEntity>().getPage(params),
                new QueryWrapper<WmsPurchaseEntity>().eq("status", 0).or().eq("status", 1)
        );

        return new PageUtils(page);


    }

    @Transactional
    @Override
    public void mergePuchase(MergeVO mergeVO) {
        Long purchaseId = mergeVO.getPuchaseId();

        if (purchaseId == null) {
            WmsPurchaseEntity purchaseEntity = new WmsPurchaseEntity();
            purchaseEntity.setStatus(WaresConstant.PurchaseStatusEnum.CREATED.getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }
        //TODO 确定采购单状态是0，1才可以合并

        List<Long> items = mergeVO.getItems();
        Long finalPurchaseId = purchaseId;
        List<WmsPurchaseDetailEntity> collect = items.stream().map(i -> {
            WmsPurchaseDetailEntity wmsPurchaseDetailEntity = new WmsPurchaseDetailEntity();
            wmsPurchaseDetailEntity.setId(i);
            wmsPurchaseDetailEntity.setPurchaseId(finalPurchaseId);
            wmsPurchaseDetailEntity.setStatus(WaresConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return wmsPurchaseDetailEntity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(collect);
        WmsPurchaseEntity wmsPurchaseEntity = new WmsPurchaseEntity();
        wmsPurchaseEntity.setId(purchaseId);
        wmsPurchaseEntity.setUpdateTime(new Date());
        this.updateById(wmsPurchaseEntity);
    }

    /**
     * 员工领取采购单
     *
     * @param ids
     */
    @Override
    public void recevied(List<Long> ids) {
//1，确定当前采购单是新建或者已分配状态
        List<WmsPurchaseEntity> collect = ids.stream().map(id -> {
            WmsPurchaseEntity byId = this.getById(id);
            return byId;
        }).filter(item -> {
            if (item.getStatus() == WaresConstant.PurchaseStatusEnum.CREATED.getCode() ||
                    item.getStatus() == WaresConstant.PurchaseStatusEnum.ASSIGNED.getCode()) {
                return true;
            }
            return false;
        }).map(item -> {
            item.setStatus(WaresConstant.PurchaseStatusEnum.RECEIVE.getCode());
            return item;
        }).collect(Collectors.toList());
        //2，改变采购单的状态
        this.updateBatchById(collect);


        //3.改变采购项的状态
     collect.forEach(item->{
//        purchaseDetailService.listdeta
     });
    }

}