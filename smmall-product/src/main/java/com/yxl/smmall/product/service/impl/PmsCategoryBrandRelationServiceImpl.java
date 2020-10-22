package com.yxl.smmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.Query;
import com.yxl.smmall.product.dao.PmsBrandDao;
import com.yxl.smmall.product.dao.PmsCategoryBrandRelationDao;
import com.yxl.smmall.product.dao.PmsCategoryDao;
import com.yxl.smmall.product.entity.PmsBrandEntity;
import com.yxl.smmall.product.entity.PmsCategoryBrandRelationEntity;
import com.yxl.smmall.product.entity.PmsCategoryEntity;
import com.yxl.smmall.product.service.PmsBrandService;
import com.yxl.smmall.product.service.PmsCategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("pmsCategoryBrandRelationService")
public class PmsCategoryBrandRelationServiceImpl extends ServiceImpl<PmsCategoryBrandRelationDao, PmsCategoryBrandRelationEntity> implements PmsCategoryBrandRelationService {
    @Autowired
    PmsBrandDao pmsBrandDao;
    @Autowired
    PmsCategoryDao pmsCategoryDao;
    @Autowired
    PmsBrandService pmsBrandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PmsCategoryBrandRelationEntity> page = this.page(
                new Query<PmsCategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<PmsCategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetail(PmsCategoryBrandRelationEntity pmsCategoryBrandRelation) {
        Long brandId = pmsCategoryBrandRelation.getBrandId();
        Long catelogId = pmsCategoryBrandRelation.getCatelogId();
        PmsBrandEntity pmsBrandEntity = pmsBrandDao.selectById(brandId);
        PmsCategoryEntity pmsCategoryEntity = pmsCategoryDao.selectById(catelogId);
        pmsCategoryBrandRelation.setBrandName(pmsBrandEntity.getName());
        pmsCategoryBrandRelation.setCatelogName(pmsCategoryEntity.getName());
        this.save(pmsCategoryBrandRelation);
    }

    @Override
    public void updateBrand(Long brandId, String name) {
        PmsCategoryBrandRelationEntity pmsCategoryBrandRelationEntity = new PmsCategoryBrandRelationEntity();
        pmsCategoryBrandRelationEntity.setBrandId(brandId);
        pmsCategoryBrandRelationEntity.setBrandName(name);
        this.update(pmsCategoryBrandRelationEntity, new UpdateWrapper<PmsCategoryBrandRelationEntity>().eq("brand_id", brandId));
    }

    @Override
    public void updataCategory(Long catId, String name) {
        this.baseMapper.updataCategory(catId, name);
    }

    @Override
    public List<PmsBrandEntity> getBrandsBycatId(Long catid) {
        List<PmsCategoryBrandRelationEntity> categoryBrandRelationEntities = this.baseMapper.selectList(new QueryWrapper<PmsCategoryBrandRelationEntity>().
                eq("catelog_id", catid));
        //获取中间表中的数据
        List<PmsBrandEntity> collect = categoryBrandRelationEntities.stream().map(item -> {
            Long brandId = item.getBrandId();
            PmsBrandEntity brandEntity = pmsBrandService.getById(brandId);
            return brandEntity;
        }).collect(Collectors.toList());
        return collect;
    }

}