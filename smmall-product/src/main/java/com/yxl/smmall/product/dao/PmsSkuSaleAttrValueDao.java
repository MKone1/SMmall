package com.yxl.smmall.product.dao;

import com.yxl.smmall.product.entity.PmsSkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yxl.smmall.product.vo.SkuItemSaleAttrVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-25 17:12:26
 */
@Mapper
public interface PmsSkuSaleAttrValueDao extends BaseMapper<PmsSkuSaleAttrValueEntity> {

    List<SkuItemSaleAttrVo> getSaleAttrSpuId(@Param("spuId") Long spuId);

    List<String> getSkuSaleAttrValueAsStringList(@Param("skuId") Long skuId);
}
