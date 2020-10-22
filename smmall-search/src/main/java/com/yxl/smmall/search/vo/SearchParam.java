package com.yxl.smmall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * 封装页面所有可能传递过来的查询条件
 * catalog3id=255&keyword=小米&sort=salecount_asc/desc&hasStock=0/1&brandId=1&brandId=2
 */
@Data
public class SearchParam {
    private String keyword;//也米娜传递过来的全文检索的关键字
    private Long catalog3Id;//三级分类ID
    /**
     * sort=saleCount_asc/desc
     * sort=skuPrice_asc/desc
     * sort=hotScore_asc/desc
     */
    private String sort;//排序条件
    /**
     * 过滤条件：
     * hasStock(是否有货),skuPrice,brandId,attrs,catalog3Id
     * hasStock=0/1
     * skuPrice=1_500/500_/_500
     * brandId=1
     * attrs=2_5寸：6寸
     */
    private Integer hasStock;//显示是否有货
    private String skuPrice;//价格区间查询
    private List<Long> brandId;//按照品牌进行查询，可以多选
    private List<String> attrs;//按照属性进行查询
    private Integer pageNumber = 1;//页面数量
    

}
