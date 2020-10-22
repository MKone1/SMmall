package com.yxl.common.to.es;

import lombok.Data;
import org.w3c.dom.Attr;

import java.math.BigDecimal;
import java.util.List;

/**ES 中的商品数据模型
 *  "properties": {
 *       "skuId": {
 *         "type": "long"
 *       },
 *       "spuId": {
 *         "type": "keyword"
 *       },
 *       "skuTitle": {
 *         "type": "text",
 *         "analyzer": "ik_smart"
 *       },
 *       "skuPrice": {
 *         "type": "keyword"
 *       },
 *       "skuImg": {
 *         "type": "keyword",
 *         "index": false,
 *         "doc_values": false
 *       },
 *       "saleCount": {
 *         "type": "long"
 *       },
 *       "hasStock": {
 *         "type": "boolean"
 *       },
 *       "hotScore": {
 *         "type": "long"
 *       },
 *       "brandId": {
 *         "type": "long"
 *       },
 *       "catelogId": {
 *         "type": "long"
 *       },
 *       "brandName": {
 *         "type": "keyword",
 *         "index": false,
 *         "doc_values": false
 *       },
 *       "brandImg": {
 *         "type": "keyword",
 *         "index": false,
 *         "doc_values": false
 *       },
 *       "cetakogId": {
 *         "type": "keyword",
 *         "index": false,
 *         "doc_values": false
 *       },
 *       "attrs": {
 *         "type": "nested",
 *         "properties": {
 *           "attrId": {
 *             "type": "long"
 *           },
 *           "attrName":{
 *             "type":"keyword",
 *             "index":false,
 *             "doc_values":false
 *           },
 *           "attrValue":{
 *             "type":"keyword"
 *           }
 *         }
 *       }
 *     }
 */
@Data
public class SkuEsModel {
    private Long spuId;
    private Long skuId;
    private String skuTitle;
    private BigDecimal skuPrice;
    private String skuImg;
    private Long saleCount;
    private Boolean hasStock;
    private Long hotscore;
    private Long brandId;
    private Long catalogId;
    private String brandName;
    private String brandImage;
    private String catalogName;
    private List<Attrs> attrs;
    @Data
    public static class Attrs{
        private Long attrId;
        private String attrName;
        private String attrValue;
    }

}
