package com.yxl.common.vo;

import javafx.beans.property.IntegerProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderSubmitVO {

    private long addrId;//收货地址的Id
    private Integer patType;//收货类型的选择
    //关于购买商品，无需提交需要购买的商品，直接去购物车在功获取一边

    //TODO：优惠信息和发票信息
    private String orderToken; // 防重令牌
    private BigDecimal payPrice;//应付价格，验价功能
    private String notes; //订单备注
    //用户的相关信息，直接可以区session去登陆的用户信息



}
