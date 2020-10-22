package com.yxl.smmall.product.service.impl;

import com.yxl.smmall.product.vo.SkuItemSaleAttrVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.Query;

import com.yxl.smmall.product.dao.PmsSkuSaleAttrValueDao;
import com.yxl.smmall.product.entity.PmsSkuSaleAttrValueEntity;
import com.yxl.smmall.product.service.PmsSkuSaleAttrValueService;


@Service("pmsSkuSaleAttrValueService")
public class PmsSkuSaleAttrValueServiceImpl extends ServiceImpl<PmsSkuSaleAttrValueDao, PmsSkuSaleAttrValueEntity> implements PmsSkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PmsSkuSaleAttrValueEntity> page = this.page(
                new Query<PmsSkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<PmsSkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuItemSaleAttrVo> getSaleAttrsByspuId(Long spuId) {

        PmsSkuSaleAttrValueDao dao = this.baseMapper;
        List<SkuItemSaleAttrVo> saleAttrVos = dao.getSaleAttrSpuId(spuId);
        return saleAttrVos;

    }

    @Override
    public List<String> getSkuSaleAttrValueAsStringList(Long skuId) {


        PmsSkuSaleAttrValueDao dao = this.baseMapper;
       return dao.getSkuSaleAttrValueAsStringList(skuId);


    }

}