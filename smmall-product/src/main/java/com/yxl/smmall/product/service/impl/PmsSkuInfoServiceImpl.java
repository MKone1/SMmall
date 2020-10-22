package com.yxl.smmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.Query;
import com.yxl.smmall.product.dao.PmsSkuInfoDao;
import com.yxl.smmall.product.entity.PmsSkuImagesEntity;
import com.yxl.smmall.product.entity.PmsSkuInfoEntity;
import com.yxl.smmall.product.entity.PmsSpuInfoDescEntity;
import com.yxl.smmall.product.service.*;
import com.yxl.smmall.product.vo.SkuInfoVo;
import com.yxl.smmall.product.vo.SkuItemSaleAttrVo;
import com.yxl.smmall.product.vo.SpuItemSaleAttrGroupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;


@Service("pmsSkuInfoService")
public class PmsSkuInfoServiceImpl extends ServiceImpl<PmsSkuInfoDao, PmsSkuInfoEntity> implements PmsSkuInfoService {
    @Autowired
    PmsSkuImagesService imagesService;
    @Autowired
    PmsSpuInfoDescService infoDescService;
    @Autowired
    PmsAttrGroupService attrGroupService;
    @Autowired
    PmsSkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    ThreadPoolExecutor executor;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PmsSkuInfoEntity> page = this.page(
                new Query<PmsSkuInfoEntity>().getPage(params),
                new QueryWrapper<PmsSkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void savrSkuInfor(PmsSkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<PmsSkuInfoEntity> wrapper = new QueryWrapper<>();
        /**
         * key: ***
         * catelogId: 225
         * brandId: 1
         * min: 0
         max: 0
         */
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((w) -> {
                w.eq("sku_id", key).or().like("sku_name", key);
            });
        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            wrapper.eq("catalog_id", catelogId);
        }


        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            wrapper.eq("brand_id", brandId);
        }

        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min) && !"0".equalsIgnoreCase(min)) {
            wrapper.ge("price", min);
        }

        String max = (String) params.get("max");
        if (!StringUtils.isEmpty(max) && !"0".equalsIgnoreCase(max)) {
            try {
                /**
                 @return -1, 0, or 1 as this {@code BigDecimal} is numerically
                  *          less than, equal to, or greater than {@code val}.
                  *          小于，等于，大于
                 */
                BigDecimal bigDecimal = new BigDecimal(max);
                if (bigDecimal.compareTo(new BigDecimal("0")) == 1) {
                    wrapper.le("price", max);
                }
            } catch (Exception e) {

            }

        }
        IPage<PmsSkuInfoEntity> page = this.page(
                new Query<PmsSkuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);

    }

    @Override
    public List<PmsSkuInfoEntity> getSkuBySpuId(Long spuId) {
        List<PmsSkuInfoEntity> skuInfoEntities = this.list(new QueryWrapper<PmsSkuInfoEntity>().eq("spu_id", spuId));
        System.out.println();
        return skuInfoEntities;
    }

    /**
     * 通过线程池的方式进行异步编排
     * @param skuId
     * @return
     */
    @Override
    public SkuInfoVo item(Long skuId) throws ExecutionException, InterruptedException {
        SkuInfoVo skuInfoVo = new SkuInfoVo();
        //通过异步编排实现优化
        CompletableFuture<PmsSkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            // 1,获取商品的基本信息，pms_sku_info
            PmsSkuInfoEntity info = getById(skuId);
            skuInfoVo.setSkuInfoEntity(info);
            return info;
        }, executor);
        CompletableFuture<Void> saleAttrFuture= infoFuture.thenAcceptAsync((res)->{
            //3,获取spu 的销售属性组合
            List<SkuItemSaleAttrVo>  saleAttrVos= skuSaleAttrValueService.getSaleAttrsByspuId(res.getSpuId());
            skuInfoVo.setAttrVos(saleAttrVos);
        },executor);
        CompletableFuture<Void> descFuture  = infoFuture.thenAcceptAsync((res) -> {
            //4，获取Spu的介绍
            PmsSpuInfoDescEntity spuInfoDescEntity = infoDescService.getById(res.getSpuId());
            skuInfoVo.setDesp(spuInfoDescEntity);
        }, executor);
        CompletableFuture<Void> baseAttrFuture = infoFuture.thenAcceptAsync((res) -> {
            //5，获取Spun的规格参数信息
            List<SpuItemSaleAttrGroupVo> attrGrounpVos = attrGroupService.getAttrGroupWithBySpuId(res.getSpuId(),
                    res.getCatalogId());
            System.out.println(attrGrounpVos.toString());
            skuInfoVo.setGrounpVoList(attrGrounpVos);
        }, executor);


        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
            //2,获取sku的图片信息
            List<PmsSkuImagesEntity> images = imagesService.getImageBySkuId(skuId);
            skuInfoVo.setImages(images);
        }, executor);

        //等待所有任务完成
        CompletableFuture.allOf(saleAttrFuture,descFuture,baseAttrFuture,imageFuture).get();



        return skuInfoVo;

    }

}