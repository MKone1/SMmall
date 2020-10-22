package com.yxl.smmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yxl.common.utils.PageUtils;
import com.yxl.smmall.product.entity.PmsSpuImagesEntity;
import com.yxl.smmall.product.entity.PmsSpuInfoEntity;

import java.util.List;
import java.util.Map;

/**
 * spu图片
 *
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-25 17:12:26
 */
public interface PmsSpuImagesService extends IService<PmsSpuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);


    void saveImages(Long id, List<String> images);
}

