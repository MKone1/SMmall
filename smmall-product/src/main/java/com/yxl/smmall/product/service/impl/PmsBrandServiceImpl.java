package com.yxl.smmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.Query;
import com.yxl.smmall.product.dao.PmsBrandDao;
import com.yxl.smmall.product.entity.PmsBrandEntity;
import com.yxl.smmall.product.service.PmsBrandService;
import com.yxl.smmall.product.service.PmsCategoryBrandRelationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


@Service("pmsBrandService")
public class PmsBrandServiceImpl extends ServiceImpl<PmsBrandDao, PmsBrandEntity> implements PmsBrandService {
    @Autowired
    PmsCategoryBrandRelationService pmsCategoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PmsBrandEntity> page = this.page(
                new Query<PmsBrandEntity>().getPage(params),
                new QueryWrapper<PmsBrandEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long brandId) {
        if (brandId == 0) {
            IPage<PmsBrandEntity> page = this.page(
                    new Query<PmsBrandEntity>().getPage(params),
                    new QueryWrapper<PmsBrandEntity>()
            );

            return new PageUtils(page);
        } else {
            String key = (String) params.get("key");
            QueryWrapper<PmsBrandEntity> wrapper = new QueryWrapper<PmsBrandEntity>().eq("brand_id", brandId)
                    .or().eq("name", key).or().like("first_letter", key);

            IPage<PmsBrandEntity> page = this.page(new Query<PmsBrandEntity>().getPage(params)
                    , wrapper);
            return new PageUtils(page);
        }

    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Object brand) {
        System.out.println(brand);
        if (brand.equals("0") || brand.equals(null)) {
            IPage<PmsBrandEntity> page = this.page(
                    new Query<PmsBrandEntity>().getPage(params),
                    new QueryWrapper<PmsBrandEntity>()
            );

            return new PageUtils(page);
        } else {
            QueryWrapper<PmsBrandEntity> wrapper = new QueryWrapper<PmsBrandEntity>().eq("brand_id", brand)
                    .or().eq("name", brand).or().like("first_letter", brand);
            IPage<PmsBrandEntity> page = this.page(new Query<PmsBrandEntity>().getPage(params)
                    , wrapper);
            return new PageUtils(page);
        }

    }

    /**
     *
     * @param pmsBrand
     */
    @Transactional //事务注解
    @Override
    public void updateDetail(PmsBrandEntity pmsBrand) {
        //保证冗余字段的一致性
        this.updateById(pmsBrand);
        if (!StringUtils.isEmpty(pmsBrand.getName())) {
pmsCategoryBrandRelationService.updateBrand(pmsBrand.getBrandId(),pmsBrand.getName());
        }
    }

}