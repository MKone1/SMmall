package com.yxl.common.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FareVO {
    private MenberAddVO memberAddressVO;
    private BigDecimal fare;
}
