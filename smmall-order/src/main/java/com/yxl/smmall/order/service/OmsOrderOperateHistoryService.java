package com.yxl.smmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yxl.common.utils.PageUtils;
import com.yxl.smmall.order.entity.OmsOrderOperateHistoryEntity;

import java.util.Map;

/**
 * 订单操作历史记录
 *
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-18 09:40:37
 */
public interface OmsOrderOperateHistoryService extends IService<OmsOrderOperateHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

