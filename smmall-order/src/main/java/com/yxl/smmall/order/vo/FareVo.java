package com.yxl.smmall.order.vo;

import com.yxl.common.vo.MenberAddVO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FareVo {
    private MenberAddVO MemberAddressVO ;
    private BigDecimal fare;
}
