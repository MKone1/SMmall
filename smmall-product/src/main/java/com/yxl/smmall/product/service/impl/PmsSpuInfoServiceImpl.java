package com.yxl.smmall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxl.common.constant.ProductConstant;
import com.yxl.common.to.SkuReductionTO;
import com.yxl.common.to.SpuBoundTO;
import com.yxl.common.to.es.SkuEsModel;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.Query;
import com.yxl.common.utils.R;
import com.yxl.common.vo.SkuHasStockVO;
import com.yxl.smmall.product.dao.PmsSpuInfoDao;
import com.yxl.smmall.product.entity.*;
import com.yxl.smmall.product.fegin.CouponFeginService;
import com.yxl.smmall.product.fegin.SearchFeginService;
import com.yxl.smmall.product.fegin.WareFeginService;
import com.yxl.smmall.product.service.*;
import com.yxl.smmall.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

//TODO 高级篇将完善该服务的错误处理机制
@Service("pmsSpuInfoService")
public class PmsSpuInfoServiceImpl extends ServiceImpl<PmsSpuInfoDao, PmsSpuInfoEntity> implements PmsSpuInfoService {

    @Autowired
    PmsSpuInfoDescService pmsSpuInfoDescService;
    @Autowired
    PmsSpuImagesService pmsSpuImagesService;
    @Autowired
    PmsAttrService pmsAttrService;
    @Autowired
    PmsProductAttrValueService pmsProductAttrValueService;
    @Autowired
    PmsSkuInfoService pmsSkuInfoService;
    @Autowired
    PmsSkuImagesService pmsSkuImagesService;
    @Autowired
    PmsSkuSaleAttrValueService pmsSkuSaleAttrValueService;
    @Autowired
    CouponFeginService couponFeginService;
    @Autowired
    PmsBrandService pmsBrandService;
    @Autowired
    PmsCategoryService pmsCategoryService;
    @Autowired
    WareFeginService wareFeginService;
    @Autowired
    SearchFeginService searchFeginService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PmsSpuInfoEntity> page = this.page(
                new Query<PmsSpuInfoEntity>().getPage(params),
                new QueryWrapper<PmsSpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 这个功能需要对多个表进行操作
     * 1，保存spu基本信息：pms-spu_info
     * 2,保存spu的描述图片:pms_spu_info_desc
     * 3，保存spu的图片集:pms_spu_images
     * 4，spu的规格参数:pms_product_attr_value
     * 5, 保存spu的积分信息：sms_spu_bounds
     * 5,保存当前对应spu对应的所有sku信息:
     * 1,sku的基本信息:pms_sku_info
     * 2，sku的图片信息：pms_sku_images
     * 3，sku的销售属性的信息：pms_sku_sale_attr_value
     * 4,sku的优惠，满减优惠：sms_sku_ladder\sms_sku_full_reduction\sms_sku_
     **/
    @Transactional  //开启事务回滚
    @Override
    public void saveSpuInfo(SpuSaveVO saveVO) {

        //1、保存spu基本信息 pms_spu_info
        PmsSpuInfoEntity infoEntity = new PmsSpuInfoEntity();
        BeanUtils.copyProperties(saveVO, infoEntity);
        infoEntity.setCreateTime(new Date());
        infoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfor(infoEntity);

        //2、保存Spu的描述图片 pms_spu_info_desc
        List<String> decript = saveVO.getDecript();
        PmsSpuInfoDescEntity descEntity = new PmsSpuInfoDescEntity();
        descEntity.setSpuId(infoEntity.getId());
        descEntity.setDecript(String.join(",", decript));
        pmsSpuInfoDescService.saveSpuInfoDesc(descEntity);


        //3、保存spu的图片集 pms_spu_images
        List<String> images = saveVO.getImages();
        pmsSpuImagesService.saveImages(infoEntity.getId(), images);


        //4、保存spu的规格参数;pms_product_attr_value
        List<BaseAttrs> baseAttrs = saveVO.getBaseAttrs();
        List<PmsProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            PmsProductAttrValueEntity valueEntity = new PmsProductAttrValueEntity();
            valueEntity.setAttrId(attr.getAttrId());
            PmsAttrEntity id = pmsAttrService.getById(attr.getAttrId());
            valueEntity.setAttrName(id.getAttrName());
            valueEntity.setAttrValue(attr.getAttrValues());
            valueEntity.setQuickShow(attr.getShowDesc());
            valueEntity.setSpuId(infoEntity.getId());

            return valueEntity;
        }).collect(Collectors.toList());
        pmsProductAttrValueService.saveProductAttr(collect);


        //5、保存spu的积分信息；gulimall_sms->sms_spu_bounds
        Bounds bounds = saveVO.getBounds();
        SpuBoundTO spuBoundTo = new SpuBoundTO();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(infoEntity.getId());
        R r = couponFeginService.saveSpuBounds(spuBoundTo);
        if (r.getCode() != 0) {
            log.error("远程保存spu积分信息失败");
        }


        //5、保存当前spu对应的所有sku信息；

        List<Skus> skus = saveVO.getSkus();
        if (skus != null && skus.size() > 0) {
            skus.forEach(item -> {
                String defaultImg = "";
                for (Images image : item.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        defaultImg = image.getImgUrl();
                    }
                }
                //    private String skuName;
                //    private BigDecimal price;
                //    private String skuTitle;
                //    private String skuSubtitle;
                PmsSkuInfoEntity skuInfoEntity = new PmsSkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(infoEntity.getBrandId());
                skuInfoEntity.setCatalogId(infoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(infoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                //5.1）、sku的基本信息；pms_sku_info
                pmsSkuInfoService.savrSkuInfor(skuInfoEntity);


                Long skuId = skuInfoEntity.getSkuId();

                List<PmsSkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
                    PmsSkuImagesEntity skuImagesEntity = new PmsSkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity -> {
                    //返回true就是需要，false就是剔除
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
                //5.2）、sku的图片信息；pms_sku_image

                pmsSkuImagesService.saveBatch(imagesEntities);
                //TODO 没有图片路径的无需保存

                List<Attr> attr = item.getAttr();
                List<PmsSkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(a -> {
                    PmsSkuSaleAttrValueEntity attrValueEntity = new PmsSkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, attrValueEntity);
                    attrValueEntity.setSkuId(skuId);

                    return attrValueEntity;
                }).collect(Collectors.toList());
                //5.3）、sku的销售属性信息：pms_sku_sale_attr_value
                pmsSkuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);
                try {

                    // //5.4）、sku的优惠、满减等信息；gulimall_sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price
                    SkuReductionTO skuReductionTo = new SkuReductionTO();
                    BeanUtils.copyProperties(item, skuReductionTo);
                    skuReductionTo.setSkuId(skuId);
                    if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1) {
                        R r1 = couponFeginService.saveSkuReduction(skuReductionTo);
                        if (r1.getCode() != 0) {
                            log.error("远程保存sku优惠信息失败");
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }


            });
        }
    }

    @Override
    public void saveBaseSpuInfor(PmsSpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    /**
     * 对SPU管理进行检索，模糊查询以及分类查询
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<PmsSpuInfoEntity> wrapper = new QueryWrapper<>();
        //进行动态判断
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((w) -> {
                w.eq("id", key).or().like("spu_name", key);
            });
        }
        //status = 1 and (id = 1 or spu_name like xxxx)
        /**status:0
         * brandId:2
         *catelogId:225
         */
        String status = (String) params.get("status");
        System.out.println(status);
        if (!StringUtils.isEmpty(status)) {
            wrapper.eq("publish_status", status);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            wrapper.eq("brand_id", brandId);

        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            wrapper.eq("catalog_id", catelogId);
        }

        IPage<PmsSpuInfoEntity> page = this.page(
                new Query<PmsSpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {

        // 4,查询当时sku的所有的规格参数可以用来检索的规格参数
        List<PmsProductAttrValueEntity> baseAttrlist =
                pmsProductAttrValueService.baseAttrlistforspu(spuId);
        List<Long> attrIds = baseAttrlist.stream().map(attr -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());
            //从数据库中查询到的可被检索的AttrID
        System.out.println("attrIds:"+attrIds.size());

       List<Long> searchList = pmsAttrService.selectSearchAttrs(attrIds);
        System.out.println("searchList:"+searchList.size());
        Set<Long> idSet = new HashSet<>(searchList);

        //通过filter过滤不可检索的Attrid,并且封装成一个Attr的list

        List<SkuEsModel.Attrs> attrsList = baseAttrlist.stream().filter(item -> {
            return idSet.contains(item.getAttrId());
        }).map(item -> {
            SkuEsModel.Attrs attrs = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(item, attrs);
            return attrs;
        }).collect(Collectors.toList());
        System.out.println("attrsList"+attrsList.size());
        //1,查出当前spuid对应的所有sku信息，品牌的名称
        List<PmsSkuInfoEntity> skuInfoEntities = pmsSkuInfoService.getSkuBySpuId(spuId);
        List<Long> skuIdList = skuInfoEntities.stream().map(PmsSkuInfoEntity::getSkuId).collect(Collectors.toList());

        skuInfoEntities.forEach(item ->{
            System.out.println(item.getCatalogId().toString());
        });
        // TODO 1，调用远程服务，库存系统判断是否还有库存，
        /**
         * 由于这里存在远程调用，可能存在网络波动以及延迟的情况导致失败，
         * 所以这里适合检查异常
         */
        Map<Long, Boolean> stockMap = new HashMap<>();
        stockMap = null;  //默认为空
        try{
            R r = wareFeginService.getSkusHasStock(skuIdList);
            //映射将SkuHasStockVO中的getSku_id作为key,遍历的所有的getHasStock作为值
            //TypeReference是受保护的，需要通过一个内部类
            TypeReference<List<SkuHasStockVO>> listTypeReference = new TypeReference<List<SkuHasStockVO>>() {
            };
            stockMap = r.getData(listTypeReference).stream().
                    collect(Collectors.toMap(SkuHasStockVO::getSku_id, item -> item.getHasstock()));

        }catch (Exception e){
            log.error("库存服务出现异常，原因：",e);
        }
        System.out.println("****"+skuInfoEntities.size());
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> collect = skuInfoEntities.stream().map(sku -> {
            //组装需要的数据
            SkuEsModel skuEsModel = new SkuEsModel();
            BeanUtils.copyProperties(sku, skuEsModel);
            System.out.println(skuEsModel.getCatalogId());
            //skuPrice,skuImg,hasStock,hotScore,等属性需要改进
            skuEsModel.setSkuPrice(sku.getPrice());
            skuEsModel.setSkuImg(sku.getSkuDefaultImg());
            //获取到当前的Id以及是否还有库存的信息
            //如果出现失败也默认为true，
            //设置库存信息
            if (finalStockMap == null){
                skuEsModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }else {
                skuEsModel.setHasStock(true);
            }

            //TODO 2，热度评分：0
            skuEsModel.setHotscore(0L);
            // 3，查询品牌和分类的名称信息
            PmsBrandEntity brand = pmsBrandService.getById(skuEsModel.getBrandId());
            skuEsModel.setBrandImage(brand.getLogo());
            skuEsModel.setBrandName(brand.getName());
            System.out.println("skuEsModel.getCatelogId()"+skuEsModel.getCatalogId());
            PmsCategoryEntity categoryEntity = pmsCategoryService.getById(skuEsModel.getCatalogId());
            skuEsModel.setCatalogName(categoryEntity.getName());

            //设置检索属性
            skuEsModel.setAttrs(attrsList);
            return skuEsModel;
        }).collect(Collectors.toList());

        /**
         * TODO 5,将数据发送给ES进行保存，保存的服务放在smmall-search中
         * 将数据通过远程调用给检索服务进行保存ES的数据模型
          */

        R r = searchFeginService.productSearch(collect);
        if (r.getCode() == 0){
            //远程调用成功
            //修改当前Spu的状态
            baseMapper.updateSPUStatus(spuId,
                    ProductConstant.StatusEnum.STATUS_ENUM_UP.getCode());
        }else{
            //远程调用失败
            // TODO ：重复调用，接口幂等性，重试机制
            /**
             * fegign调用流程：SynchronousMethodHandler.class
             *   1.构造请求函数，将对象转为JSON
             * RequestTemplate template = this.buildTemplateFromArgs.create(argv);
             * 2，发送请求进行执行（执行成功会解码响应数据）；
             *  executeAndDecode(template, options)
             *  3，执行请求会有重试机制
             *   Retryer retryer = this.retryer.clone();
             *   定义一个重试器，
             *   while(true) {
             *             try {
             *                 return this.executeAndDecode(template, options);
             *             } catch (RetryableException var9) {
             *                 RetryableException e = var9;
             *                 try {
             *                     retryer.continueOrPropagate(e);  //继续执行
             *                 } catch (RetryableException var8) {
             *                 //当重试器抛出异常时，则不能继续执行
             *                     Throwable cause = var8.getCause();
             *                     if (this.propagationPolicy == ExceptionPropagationPolicy.UNWRAP && cause != null) {
             *                         throw cause;
             *                     }
             *                     throw var8;
             *                 }
             *                 if (this.logLevel != Level.NONE) {
             *                     this.logger.logRetry(this.metadata.configKey(), this.logLevel);
             *                 }
             *             }
             */
        }

    }

    @Override
    public PmsSpuInfoEntity getSpuInfoBySkuId(Long skuId) {
        PmsSkuInfoEntity skuInfoEntity = pmsSkuInfoService.getById(skuId);
        return getById(skuInfoEntity.getSpuId());


    }

}