package com.yxl.smmall.product.dao;

import com.yxl.smmall.product.entity.PmsCategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品三级分类
 * 
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-25 17:12:26
 */
@Mapper
public interface PmsCategoryDao extends BaseMapper<PmsCategoryEntity> {

    List<PmsCategoryEntity> selectByLavel1();
}
