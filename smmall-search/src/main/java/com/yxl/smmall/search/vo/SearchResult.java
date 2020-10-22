package com.yxl.smmall.search.vo;

import com.yxl.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.List;

@Data
public class SearchResult {
    //查询到的商品的所有信息
    private List<SkuEsModel> products;
    /**
     * 以下是分页信息
     */

    private Integer pageNumber;//当前页码
    private Long total;//总记录数
    private Integer totalPages;//总页码
    private List<Integer> pageNavs;


    private List<BrandVo> brands;//当前查询的结果，所有涉及到的品牌
    private List<CatelogVo> catelogs;//当前查询到的结果，所有涉及到的分类
    private List<AttrVo> attrs;//当前查询的结果，所有涉及到的所有属性
    // ====================以下是返回页面的所有信息==========
    @Data
    public static class BrandVo {

        private Long brandId;

        private String brandName;

        private String brandImg;

    }

    /**
     * 属性信息
     * 当前查询到的结果 所有涉及到的属性
     */

    @Data
    public static class AttrVo {

        private Long attrId;

        private String attrName;

        private List<String> attrValue;

    }

    /**
     * 分类信息
     * 当前查询到的结果 所有涉及到的分类
     */

    @Data
    public static class CatelogVo {

        private Long catalogId;

        private String catalogName;

    }
}
