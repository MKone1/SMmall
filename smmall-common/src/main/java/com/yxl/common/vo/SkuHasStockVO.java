package com.yxl.common.vo;

import lombok.Data;
import org.slf4j.Logger;

@Data
public class SkuHasStockVO {
    private Long sku_id;
    private Boolean hasstock;
}
