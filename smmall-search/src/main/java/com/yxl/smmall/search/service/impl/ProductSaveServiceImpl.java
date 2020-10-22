package com.yxl.smmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.yxl.common.to.es.SkuEsModel;
import com.yxl.smmall.search.config.SMmallElasticSearchConfig;
import com.yxl.smmall.search.constant.ESConstant;
import com.yxl.smmall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author SADSADSD
 */
@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {
    @Qualifier("esRestClient")
    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Override
    public Boolean saveEsModel(List<SkuEsModel> skuEsModels) throws IOException {
        /**
         * 保存到ES中，
         * 1，给ES建立索引，添加映射二
         * 2， 在Es中保存这些数据
         */
        //BulkRequest bulkRequest, RequestOptions options
        BulkRequest bulkRequest = new BulkRequest();
        //通过循环遍历，将skuEsModels
        /**
         * public BulkRequest add(IndexRequest request) {
         *         return this.internalAdd(request);
         *     }
         */
        for (SkuEsModel model : skuEsModels) {
            IndexRequest indexRequest = new IndexRequest(ESConstant.PRODUCT_INDEX_NAME);
            indexRequest.id(model.getSkuId().toString());
            String s = JSON.toJSONString(model);
            System.out.println("JSONmodel:"+s);
            indexRequest.source(s, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }

        System.out.println(bulkRequest);
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, SMmallElasticSearchConfig.COMMON_OPTIONS);
        // 如果批量保存失败

        boolean b = bulk.hasFailures();
        List<String> collect = Arrays.stream(bulk.getItems()).map(item -> {
            return item.getId();
        }).collect(Collectors.toList());
        log.info("商品上架成功：",collect);

        return b;
    }

}
