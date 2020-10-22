package com.yxl.smmall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.Query;

import com.yxl.smmall.product.dao.PmsProductAttrValueDao;
import com.yxl.smmall.product.entity.PmsProductAttrValueEntity;
import com.yxl.smmall.product.service.PmsProductAttrValueService;
import org.springframework.transaction.annotation.Transactional;


@Service("pmsProductAttrValueService")
public class PmsProductAttrValueServiceImpl extends ServiceImpl<PmsProductAttrValueDao, PmsProductAttrValueEntity> implements PmsProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PmsProductAttrValueEntity> page = this.page(
                new Query<PmsProductAttrValueEntity>().getPage(params),
                new QueryWrapper<PmsProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveProductAttr(List<PmsProductAttrValueEntity> collect) {
        this.saveBatch(collect);
    }

    @Override
    public List<PmsProductAttrValueEntity> baseAttrlistforspu(Long spuId) {
        List<PmsProductAttrValueEntity> entities = this.baseMapper.selectList(new QueryWrapper<PmsProductAttrValueEntity>().eq("spu_id", spuId));
        return entities;
    }

    @Transactional
    @Override
    public void updateSpuAttr(Long spuId, List<PmsProductAttrValueEntity> entities) {
        //1、删除这个spuId之前对应的所有属性
        this.baseMapper.delete(new QueryWrapper<PmsProductAttrValueEntity>().eq("spu_id",spuId));


        List<PmsProductAttrValueEntity> collect = entities.stream().map(item -> {
            item.setSpuId(spuId);
            return item;
        }).collect(Collectors.toList());
        this.saveBatch(collect);
    }

}