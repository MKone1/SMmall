package com.yxl.smmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.Query;
import com.yxl.smmall.product.dao.PmsSkuImagesDao;
import com.yxl.smmall.product.entity.PmsSkuImagesEntity;
import com.yxl.smmall.product.service.PmsSkuImagesService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("pmsSkuImagesService")
public class PmsSkuImagesServiceImpl extends ServiceImpl<PmsSkuImagesDao, PmsSkuImagesEntity> implements PmsSkuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PmsSkuImagesEntity> page = this.page(
                new Query<PmsSkuImagesEntity>().getPage(params),
                new QueryWrapper<PmsSkuImagesEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<PmsSkuImagesEntity> getImageBySkuId(Long skuId) {

        PmsSkuImagesDao baseMapper = this.baseMapper;

        return baseMapper.selectList(new QueryWrapper<PmsSkuImagesEntity>().eq("sku_id", skuId));
    }
}

