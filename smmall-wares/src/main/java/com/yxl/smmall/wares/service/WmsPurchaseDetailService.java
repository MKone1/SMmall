package com.yxl.smmall.wares.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yxl.common.utils.PageUtils;
import com.yxl.smmall.wares.entity.WmsPurchaseDetailEntity;

import java.util.Map;

/**
 * 
 *
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-18 09:52:15
 */
public interface WmsPurchaseDetailService extends IService<WmsPurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

