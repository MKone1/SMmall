package com.yxl.smmall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;
@ToString
@Data
public class SkuItemSaleAttrVo {
    private Long attrId;
    private String attrName;
    private List<AttrValueWithSkuIdVo> attrValues;

}
