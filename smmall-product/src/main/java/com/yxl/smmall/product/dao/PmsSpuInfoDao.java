package com.yxl.smmall.product.dao;

import com.yxl.smmall.product.entity.PmsSpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import feign.Param;
import org.apache.ibatis.annotations.Mapper;

/**
 * spu信息
 * 
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-25 17:12:26
 */
@Mapper
public interface PmsSpuInfoDao extends BaseMapper<PmsSpuInfoEntity> {

    void updateSPUStatus(@Param("spuId")Long spuId, @Param("code") int code);


}
