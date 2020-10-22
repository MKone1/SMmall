package com.yxl.smmall.order.to;

import com.yxl.smmall.order.entity.OmsOrderEntity;
import com.yxl.smmall.order.entity.OmsOrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreateTo {

    private OmsOrderEntity orderEntity;//订单实体类
    private List<OmsOrderItemEntity> orderItemList;
    private BigDecimal payPrice;
    private BigDecimal fare;

}



