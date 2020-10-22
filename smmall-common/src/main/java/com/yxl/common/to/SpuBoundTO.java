package com.yxl.common.to;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class SpuBoundTO {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
