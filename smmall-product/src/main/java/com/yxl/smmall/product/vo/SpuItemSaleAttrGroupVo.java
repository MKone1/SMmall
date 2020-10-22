package com.yxl.smmall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;
@ToString
@Data
public  class SpuItemSaleAttrGroupVo {
    private String groupName;
    private List<Attr> attrs;
}

