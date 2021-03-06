package com.yxl.smmall.product.dao;

import com.yxl.smmall.product.entity.PmsAttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 * 
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-25 17:12:26
 */
@Mapper
public interface PmsAttrDao extends BaseMapper<PmsAttrEntity> {

    List<Long> selectSearchAttrByIds(@Param("attrIds") List<Long> attrIds);
}
