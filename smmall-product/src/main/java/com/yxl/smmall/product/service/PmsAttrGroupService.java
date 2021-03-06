package com.yxl.smmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yxl.common.utils.PageUtils;
import com.yxl.smmall.product.entity.PmsAttrGroupEntity;
import com.yxl.smmall.product.vo.AttrGroupWithAttrsVO;
import com.yxl.smmall.product.vo.SkuInfoVo;
import com.yxl.smmall.product.vo.SpuItemSaleAttrGroupVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-25 17:12:26
 */
public interface PmsAttrGroupService extends IService<PmsAttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    List<AttrGroupWithAttrsVO> getAttrGroupWithByCatelogId(Long catLogId);

    List<SpuItemSaleAttrGroupVo> getAttrGroupWithBySpuId(Long spuId, Long catalogId);
}

