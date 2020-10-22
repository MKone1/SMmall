package com.yxl.smmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.vo.CheckOutPageVO;
import com.yxl.common.vo.OrderSubmitVO;
import com.yxl.smmall.order.entity.OmsOrderEntity;
import com.yxl.smmall.order.vo.SubmitOrderResponseVO;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-18 09:40:37
 */
public interface OmsOrderService extends IService<OmsOrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    CheckOutPageVO checkOut() throws ExecutionException, InterruptedException;

    SubmitOrderResponseVO submitOrder(OrderSubmitVO vo);

    OmsOrderEntity getOrderByOrderSn(String s);
}

