package com.yxl.smmall.product.dao;

import com.yxl.smmall.product.entity.PmsAttrAttrgroupRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性&属性分组关联
 * 
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-25 17:12:26
 */
@Mapper
public interface PmsAttrAttrgroupRelationDao extends BaseMapper<PmsAttrAttrgroupRelationEntity> {
	void insertAttrGroup(@Param("attrId") Long attrId, @Param("attrGroupId") Long attrGroupId);

    void deleteBatchRelation(@Param("collect") List<PmsAttrAttrgroupRelationEntity> collect) throws Exception;
}
