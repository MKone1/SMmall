package com.yxl.smmall.product.dao;

import com.yxl.smmall.product.entity.PmsCategoryBrandRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 品牌分类关联
 * 
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-25 17:12:26
 */
@Mapper
public interface PmsCategoryBrandRelationDao extends BaseMapper<PmsCategoryBrandRelationEntity> {

    void updataCategory(@Param("catId") Long catId, @Param("name") String name);

}
