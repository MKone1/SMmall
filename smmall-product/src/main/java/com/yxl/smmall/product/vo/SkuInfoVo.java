package com.yxl.smmall.product.vo;

import com.yxl.smmall.product.entity.PmsSkuImagesEntity;
import com.yxl.smmall.product.entity.PmsSkuInfoEntity;
import com.yxl.smmall.product.entity.PmsSpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * 商品详情页的返回数据模型
 *
 * @author SADSADSD
 */
@Data
public class SkuInfoVo {
    // 1，sku基本信息获取， pms_sku_ifo
    PmsSkuInfoEntity skuInfoEntity;
    //2,sku图片信息 pms_sku-image
    List<PmsSkuImagesEntity> images;
    //3,获取spu的介绍
    List<SkuItemSaleAttrVo> attrVos;
    //4，获取spu的销售属性组合
    PmsSpuInfoDescEntity desp;
    //5，获取spu的规格参数信息
    List<SpuItemSaleAttrGroupVo> grounpVoList;
    //6,是否有货
    boolean hasStock = true;



}
