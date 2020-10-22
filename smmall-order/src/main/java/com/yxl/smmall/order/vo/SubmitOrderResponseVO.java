package com.yxl.smmall.order.vo;

import com.yxl.smmall.order.entity.OmsOrderEntity;
import lombok.Data;

@Data
public class SubmitOrderResponseVO {
    private OmsOrderEntity omsOrderEntity;
    private Integer code;

}
