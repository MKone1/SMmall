package com.yxl.smmall.product.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.Query;

import com.yxl.smmall.product.dao.PmsProductAttrValueDao;
import com.yxl.smmall.product.entity.PmsProductAttrValueEntity;
import com.yxl.smmall.product.service.PmsProductAttrValueService;


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

}