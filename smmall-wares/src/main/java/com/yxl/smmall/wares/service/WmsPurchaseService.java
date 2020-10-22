package com.yxl.smmall.wares.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.vo.MergeVO;
import com.yxl.smmall.wares.entity.WmsPurchaseEntity;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-18 09:52:15
 */
public interface WmsPurchaseService extends IService<WmsPurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnReceive(Map<String, Object> params);

    void mergePuchase(MergeVO mergeVO);

    void recevied(List<Long> ids);
}

