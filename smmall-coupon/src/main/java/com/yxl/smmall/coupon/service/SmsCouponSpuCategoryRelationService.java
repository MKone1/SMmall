package com.yxl.smmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yxl.common.utils.PageUtils;
import com.yxl.smmall.coupon.entity.SmsCouponSpuCategoryRelationEntity;

import java.util.Map;

/**
 * 优惠券分类关联
 *
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-18 08:50:56
 */
public interface SmsCouponSpuCategoryRelationService extends IService<SmsCouponSpuCategoryRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

