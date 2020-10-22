package com.yxl.smmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yxl.common.utils.PageUtils;
import com.yxl.smmall.product.entity.PmsSpuInfoDescEntity;
import com.yxl.smmall.product.entity.PmsSpuInfoEntity;
import com.yxl.smmall.product.vo.SpuSaveVO;

import java.util.Map;

/**
 * spu信息
 *
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-25 17:12:26
 */
public interface PmsSpuInfoService extends IService<PmsSpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVO saveVO);

    void saveBaseSpuInfor(PmsSpuInfoEntity spuInfoEntity);


    PageUtils queryPageByCondition(Map<String, Object> params);

    /**
     * 实现商品的商家服务
     * @param spuId
     */
    void up(Long spuId);

    PmsSpuInfoEntity getSpuInfoBySkuId(Long skuId);
}

