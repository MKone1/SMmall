package com.yxl.smmall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxl.common.constant.OrderConstant;
import com.yxl.common.constant.OrderStatusEnum;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.Query;
import com.yxl.common.utils.R;
import com.yxl.common.vo.*;
import com.yxl.smmall.order.dao.OmsOrderDao;
import com.yxl.smmall.order.entity.OmsOrderEntity;
import com.yxl.smmall.order.entity.OmsOrderItemEntity;
import com.yxl.smmall.order.exception.NoStockException;
import com.yxl.smmall.order.fegin.MenberAddFegin;
import com.yxl.smmall.order.fegin.OrderProducesFegin;
import com.yxl.smmall.order.fegin.ProdectFegin;
import com.yxl.smmall.order.fegin.WareFeign;
import com.yxl.smmall.order.interceptor.LoginUserinterceptor;
import com.yxl.smmall.order.service.OmsOrderItemService;
import com.yxl.smmall.order.service.OmsOrderService;
import com.yxl.smmall.order.to.OrderCreateTo;
import com.yxl.smmall.order.vo.SubmitOrderResponseVO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@Service("omsOrderService")
public class OmsOrderServiceImpl extends ServiceImpl<OmsOrderDao, OmsOrderEntity> implements OmsOrderService {
    //线程数据共享
    private ThreadLocal<OrderSubmitVO> checkOutPageVOThreadLocal = new ThreadLocal<>();

    @Autowired
    MenberAddFegin menberAddFegin;
    //    @Autowired
//    CouponFegin couponFegin;
    @Autowired
    ThreadPoolExecutor executor;
    @Autowired
    OrderProducesFegin orderProducesFegin;
    @Autowired
    WareFeign wareFeign;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ProdectFegin prodectFegin;
    @Autowired
    OmsOrderItemService omsOrderItemService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    //
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OmsOrderEntity> page = this.page(
                new Query<OmsOrderEntity>().getPage(params),
                new QueryWrapper<OmsOrderEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 实现结算页面数据
     * 通过异步编排实现，在获取会员地址的同时获取所选中的购物项，并进行计算
     * TODO：关于远程调用fegin出现的远程调用丢失请求头，以及fegin在异步编排中出现丢失上下文
     *
     * @return
     */
    @Override
    public CheckOutPageVO checkOut() throws ExecutionException, InterruptedException {
        CheckOutPageVO checkOutPageVO = new CheckOutPageVO();
        AtomicReference<MenberAddVO> addressVO = new AtomicReference<>(new MenberAddVO());
        MemberRespVo memberRespVo = LoginUserinterceptor.loginUserResp.get();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        CompletableFuture<Void> receiveAddress = CompletableFuture.runAsync(() -> {
            //1,调用远程服务的接口实现获取会员地址
            RequestContextHolder.setRequestAttributes(requestAttributes);
            Long id = memberRespVo.getId();
            R info = menberAddFegin.infoByUserId(id);
            if (info.getCode() == 0) {
                List<MenberAddVO> address = info.getData("umsMemberReceiveAddress", new TypeReference<List<MenberAddVO>>() {
                });
                for (MenberAddVO menberAddVO : address) {
                    Integer defaultStatus = menberAddVO.getDefaultStatus();
                    if (defaultStatus == 1) {
                        addressVO.set(menberAddVO);
                    }
                }
                checkOutPageVO.setAddresses(address);
            }
        }, executor);


        //2,调用远程服务实现将被选中的购物项获取过来并计算
        //feign在远程调用之前要构造请求，调用很多拦截器
        CompletableFuture<Void> CartCompletavble = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> cartItems = orderProducesFegin.CartItems();
            checkOutPageVO.setItems(cartItems);
        }, executor).thenRunAsync(() -> {
            //远程调用库存服务，查询购物车中商品库存
            List<OrderItemVo> items = checkOutPageVO.getItems();
            List<Long> collect = items.stream().map(item -> item.getSkuId()).collect(Collectors.toList());
            //调用远程服务，批量查询
            R stock = wareFeign.getSkusHasStock(collect);
            List<SkuHasStockVO> data = stock.getData(new TypeReference<List<SkuHasStockVO>>() {
            });
            if (data != null) {
                Map<Long, Boolean> Map = data.stream().collect(Collectors.toMap(SkuHasStockVO::getSku_id, SkuHasStockVO::getHasstock));
                checkOutPageVO.setStocks(Map);
            }
        }, executor);

        //TODO：查询相关商品的优惠卷信息，积分信息,修改BUG信息

        //3,查询积分
        Integer integration = memberRespVo.getIntegration();
        checkOutPageVO.setIntegration(integration);
// 4,其他数据自动计算

        //TODO：5，防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        stringRedisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId(),
                token, 30, TimeUnit.MINUTES);
        checkOutPageVO.setOrderToken(token);

        CompletableFuture.allOf(CartCompletavble, receiveAddress).get();
        return checkOutPageVO;
    }

    /**
     * 下单，创建订单，验证令牌，。。。。
     *@Transactional 是本地治事务，在分布式系统中，只能在控制住自己的回滚，控制不了其他服务的回滚
     *  分布式事务：使用分布式事务最大的原因是网络问题；
     *
     * @param vo
     * @return
     */
    @Transactional
    @Override
    public SubmitOrderResponseVO submitOrder(OrderSubmitVO vo) {
        checkOutPageVOThreadLocal.set(vo);
        SubmitOrderResponseVO submitOrderResponseVO = new SubmitOrderResponseVO();
        MemberRespVo memberRespVo = LoginUserinterceptor.loginUserResp.get();
        //验证令牌必须保证令牌的对比和删除保证操作的原子性
        //通过code表达是否验证成功，0验证令牌成功,并删除令牌，1验证令牌失败
        submitOrderResponseVO.setCode(0);
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String orderToken = vo.getOrderToken();
        // 原子性操作验证和删除令牌，使用redis中的LUA脚本来实现
        Long result = stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class),
                Collections.singletonList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId()), orderToken);
        if (result == 0L) {
            // 令牌验证失败
            submitOrderResponseVO.setCode(1);
            return submitOrderResponseVO;
        } else {
            //令牌验证成功
            //下单，去创建订单，验证令牌，验价格，锁库存。。。。
            //由于传递参数过于多，所以，开启该线程共享
            //1,创建订单，订单项等数据
            OrderCreateTo order = createOrder();
            //2，验价
            BigDecimal payAmount = order.getOrderEntity().getPayAmount();
            BigDecimal payPrice = vo.getPayPrice();
            if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
                //金额对比
                //3,保存订单
                saveOrder(order);
                //4,调用远程服务实现锁库存，只要有异常就会发生回滚
                //订单号，所有订单项

                WareSkuLockVo lockVO = new WareSkuLockVo();
                lockVO.setOrderSn(order.getOrderEntity().getOrderSn());
                List<OrderItemVo> locks = order.getOrderItemList().stream().map(o -> {
                    OrderItemVo itemVO = new OrderItemVo();
                    itemVO.setSkuId(o.getSkuId());
                    itemVO.setCount(o.getSkuQuantity());
                    itemVO.setTitle(o.getSkuName());
                    return itemVO;
                }).collect(Collectors.toList());
                lockVO.setLocks(locks);
                //远程所库存
                R r = wareFeign.orderLockStock(lockVO);
                if (r.getCode() == 0) {
                    // 锁定成功
                    submitOrderResponseVO.setOmsOrderEntity(order.getOrderEntity());
                    rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", order.getOrderEntity());
                    return submitOrderResponseVO;
                } else {
                    // 锁定失败
                    submitOrderResponseVO.setCode(3);
                  return submitOrderResponseVO;
                }
            } else {
                submitOrderResponseVO.setCode(2);
                return submitOrderResponseVO;
            }

        }

    }

    @Override
    public OmsOrderEntity getOrderByOrderSn(String s) {

        OmsOrderEntity order_sn = this.getOne(new QueryWrapper<OmsOrderEntity>().eq("order_sn", s));


return order_sn;
    }

    /**
     * 创建订单
     *
     * @return
     */
    private OrderCreateTo createOrder() {
        OrderCreateTo orderCreateTo = new OrderCreateTo();
        //1,生成订单id;
        String ordersn = IdWorker.getTimeId();
        OmsOrderEntity omsOrderEntity = buildOrder(ordersn);
        orderCreateTo.setOrderEntity(omsOrderEntity);
        //2,获取收货地址信息
        List<OmsOrderItemEntity> omsOrderItemEntities = buildOrderItems(ordersn);
        orderCreateTo.setOrderItemList(omsOrderItemEntities);
        computePrice(omsOrderEntity, omsOrderItemEntities);


        //


        return orderCreateTo;
    }

    /**
     * 保存订单
     *
     * @param orderCreateTO
     */
    private void saveOrder(OrderCreateTo orderCreateTO) {
        OmsOrderEntity orderEntity = orderCreateTO.getOrderEntity();
        orderEntity.setModifyTime(new Date());
        this.save(orderEntity);
        List<OmsOrderItemEntity> orderItems = orderCreateTO.getOrderItemList();
        omsOrderItemService.saveBatch(orderItems);
    }

    /**
     * 设置 计算所有的价格和积分
     *
     * @param orderEntity
     * @param orderItemEntities
     */
    private void computePrice(OmsOrderEntity orderEntity, List<OmsOrderItemEntity> orderItemEntities) {
        BigDecimal total = new BigDecimal("0.0");
        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal integration = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");

        BigDecimal gift = new BigDecimal("0.0");
        BigDecimal growth = new BigDecimal("0.0");
        // 订单的总额，叠加每一个订单项的总额信息。
        for (OmsOrderItemEntity entity : orderItemEntities) {
            coupon = coupon.add(entity.getCouponAmount());
            integration = integration.add(entity.getIntegrationAmount());
            promotion = promotion.add(entity.getPromotionAmount());
            total = total.add(entity.getRealAmount());

            gift = gift.add(new BigDecimal(entity.getGiftIntegration().toString()));
            growth = growth.add(new BigDecimal(entity.getGiftGrowth().toString()));
        }
        // 订单价格相关
        orderEntity.setTotalAmount(total);
        // 应付金额
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(integration);
        orderEntity.setCouponAmount(coupon);

        // 设置积分信息
        orderEntity.setIntegration(gift.intValue());
        orderEntity.setGrowth(growth.intValue());

        orderEntity.setDeleteStatus(0);
    }


    /**
     * 构建订单
     *
     * @param orderSn
     * @return
     */
    private OmsOrderEntity buildOrder(String orderSn) {
        MemberRespVo memberRespVo = LoginUserinterceptor.loginUserResp.get();
        OmsOrderEntity omsOrderEntity = new OmsOrderEntity();
        // 创建订单号
        omsOrderEntity.setOrderSn(orderSn);
        omsOrderEntity.setMemberId(memberRespVo.getId());

        OrderSubmitVO orderSubmitVO = checkOutPageVOThreadLocal.get();

        // 获取收货地址信息
        R fare = wareFeign.getFare(orderSubmitVO.getAddrId());
        FareVO fareResponse = fare.getData(new TypeReference<FareVO>() {
        });

        // 设置运费信息
        omsOrderEntity.setFreightAmount(fareResponse.getFare());
        // 设置收货人信息
        omsOrderEntity.setReceiverCity(fareResponse.getMemberAddressVO().getCity());
        omsOrderEntity.setReceiverDetailAddress(fareResponse.getMemberAddressVO().getDetailAddress());
        omsOrderEntity.setReceiverName(fareResponse.getMemberAddressVO().getName());
        omsOrderEntity.setReceiverPhone(fareResponse.getMemberAddressVO().getPhone());
        omsOrderEntity.setReceiverPostCode(fareResponse.getMemberAddressVO().getPostCode());
        omsOrderEntity.setReceiverProvince(fareResponse.getMemberAddressVO().getProvince());
        omsOrderEntity.setReceiverRegion(fareResponse.getMemberAddressVO().getRegion());

        // 设置订单的状态信息
        omsOrderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        omsOrderEntity.setAutoConfirmDay(7);


        return omsOrderEntity;
    }


    /**
     * 构建所有订单项数据
     *
     * @param orderSn
     * @return
     */
    private List<OmsOrderItemEntity> buildOrderItems(String orderSn) {
        List<OrderItemVo> currentUserCartItems = orderProducesFegin.CartItems();
        if (!CollectionUtils.isEmpty(currentUserCartItems)) {
            List<OmsOrderItemEntity> itemEntities = currentUserCartItems.stream().map(cartItem -> {
                OmsOrderItemEntity orderItemEntity = buildOrderItem(cartItem);
                orderItemEntity.setOrderSn(orderSn);
                return orderItemEntity;
            }).collect(Collectors.toList());
            return itemEntities;
        }
        return null;
    }

    /**
     * 构建每一个订单项数据
     *
     * @param cartItem
     * @return
     */
    private OmsOrderItemEntity buildOrderItem(OrderItemVo cartItem) {
        OmsOrderItemEntity OmsOrderItemEntity = new OmsOrderItemEntity();
        // 1 订单信息 订单号
        // 2 SPU信息
        Long skuId = cartItem.getSkuId();
        R r = prodectFegin.getSpuInfoBySkuId(skuId);
        SpuInfoVO data = r.getData(new TypeReference<SpuInfoVO>() {
        });
        OmsOrderItemEntity.setSpuId(data.getId());
        OmsOrderItemEntity.setSpuBrand(data.getBrandId().toString());
        OmsOrderItemEntity.setSpuName(data.getSpuName());
        OmsOrderItemEntity.setCategoryId(data.getCatalogId());

        // 3 SKU信息
        OmsOrderItemEntity.setSkuId(cartItem.getSkuId());
        OmsOrderItemEntity.setSkuName(cartItem.getTitle());
        OmsOrderItemEntity.setSkuPic(cartItem.getImageUrl());
        OmsOrderItemEntity.setSkuPrice(cartItem.getPrice());
        String skuAttrs = StringUtils.collectionToDelimitedString(cartItem.getSkuAttr(), ";");
        OmsOrderItemEntity.setSkuAttrsVals(skuAttrs);
        OmsOrderItemEntity.setSkuQuantity(cartItem.getCount());
        // 4 优惠信息 [不做]

        // 5 积分信息
        OmsOrderItemEntity.setGiftGrowth(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());
        OmsOrderItemEntity.setGiftIntegration(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());

        // 6 订单的价格信息
        OmsOrderItemEntity.setPromotionAmount(new BigDecimal("0"));
        OmsOrderItemEntity.setIntegrationAmount(new BigDecimal("0"));
        OmsOrderItemEntity.setCouponAmount(new BigDecimal("0"));
        // 当前订单项的实际金额
        BigDecimal origin = OmsOrderItemEntity.getSkuPrice().multiply(new BigDecimal(OmsOrderItemEntity.getSkuQuantity().toString()));
        // 总额减去各种优惠后的价格
        BigDecimal subtract = origin.subtract(OmsOrderItemEntity.getCouponAmount()).subtract(OmsOrderItemEntity.getIntegrationAmount()).subtract(OmsOrderItemEntity.getPromotionAmount());
        OmsOrderItemEntity.setRealAmount(subtract);
        return OmsOrderItemEntity;
    }

}
//获取用户的会员信息
//            Long levelId = memberRespVo.getLevelId();
//            //获取当前时间
//            Date date = new Date();
//            //查询优惠卷信息（调用远程服务）
//            R coupon = couponFegin.getCoupon();
//            if (coupon.getCode() == 0) {
//                //表示没有优惠卷
//            } else {
//                //有有优惠卷，进行筛选
//                List<CouponVO> getCouponList = coupon.getData("getCouponList", new TypeReference<List<CouponVO>>() {
//                });
//                List<CouponVO> collect = getCouponList.stream().filter(item -> item.getMemberLevel() == 0 && item.getMemberLevel().equals(levelId) && item.getEnableEndTime().compareTo(date) >= 0)
//                        .collect(Collectors.toList());
//
//
//            }
