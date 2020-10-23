package com.yxl.smmall.wares.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yxl.common.utils.PageUtils;
import com.yxl.smmall.wares.entity.WmsWareOrderTaskEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-18 09:52:15
 */
public interface WmsWareOrderTaskService extends IService<WmsWareOrderTaskEntity> {

    PageUtils queryPage(Map<String, Object> params);

    WmsWareOrderTaskEntity getOrderTaskByOrderSn(String orderSn);
}

