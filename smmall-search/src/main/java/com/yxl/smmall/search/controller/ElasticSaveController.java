package com.yxl.smmall.search.controller;

import com.yxl.common.exception.BizCodeEnume;
import com.yxl.common.to.es.SkuEsModel;
import com.yxl.common.utils.R;
import com.yxl.smmall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *@author SADSADSD
 * @RestController 返回一个Json字符串
 */
@Slf4j
@RequestMapping("/search/save")
@RestController
public class ElasticSaveController {
    @Autowired
    ProductSaveService productSaveService;
    @PostMapping("/product")
    public R productSearch(@RequestBody List<SkuEsModel> skuEsModels){
        Boolean b ;

        try{
          b = productSaveService.saveEsModel(skuEsModels);

        }catch (Exception e){
            log.error("ElasticSaveController商品上架报错：",e);
            return R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnume.PRODUCT_UP_EXCEPTION.getMsg());
        }
        if (!b){
            return R.ok();
        }else {
            return R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnume.PRODUCT_UP_EXCEPTION.getMsg());
        }

    }


}
