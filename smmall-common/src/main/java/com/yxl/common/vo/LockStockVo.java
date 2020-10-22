package com.yxl.common.vo;

import lombok.Data;

@Data
public class LockStockVo {
    private Long SkuId;
    private Integer number;
    private Boolean locked;
}
