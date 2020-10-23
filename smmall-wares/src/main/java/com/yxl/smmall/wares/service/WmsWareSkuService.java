package com.yxl.smmall.wares.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yxl.common.to.mq.SrockLockedTo;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.vo.LockStockVo;
import com.yxl.common.vo.OrderVo;
import com.yxl.common.vo.SkuHasStockVO;
import com.yxl.common.vo.WareSkuLockVo;
import com.yxl.smmall.wares.entity.WmsWareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-18 09:52:15
 */
public interface WmsWareSkuService extends IService<WmsWareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByCondition(Map<String, Object> params);

    List<SkuHasStockVO> getSkusHasStock(List<Long> skuIdList);

 Boolean orderlockstock(WareSkuLockVo vo);

    void unlockStock(SrockLockedTo to);

    void unlockStock(OrderVo orderVo);
}

