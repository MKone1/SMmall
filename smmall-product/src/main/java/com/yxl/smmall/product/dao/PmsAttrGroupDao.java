package com.yxl.smmall.product.dao;

import com.yxl.smmall.product.entity.PmsAttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yxl.smmall.product.vo.SkuInfoVo;
import com.yxl.smmall.product.vo.SpuItemSaleAttrGroupVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 * 
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-25 17:12:26
 */
@Mapper
public interface PmsAttrGroupDao extends BaseMapper<PmsAttrGroupEntity> {

    List<SpuItemSaleAttrGroupVo> getAttrGroupWithBySpuId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);
}
