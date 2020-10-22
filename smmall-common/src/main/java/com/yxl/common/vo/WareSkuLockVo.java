package com.yxl.common.vo;

import lombok.Data;

import java.util.List;

@Data
public class WareSkuLockVo {

    private String orderSn;//订单号
    private List<OrderItemVo> locks;//需要锁住的商品信息
}
