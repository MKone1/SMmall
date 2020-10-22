package com.yxl.smmall.search.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sun.xml.internal.bind.v2.model.core.EnumConstant;
import com.yxl.common.to.es.SkuEsModel;
import com.yxl.smmall.search.config.SMmallElasticSearchConfig;
import com.yxl.smmall.search.constant.ESConstant;
import com.yxl.smmall.search.service.MallSearchService;
import com.yxl.smmall.search.vo.SearchParam;
import com.yxl.smmall.search.vo.SearchResult;
import jdk.nashorn.internal.runtime.linker.LinkerCallSite;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
public class MallSearchServiceImpl implements MallSearchService {
    @Autowired
    private RestHighLevelClient client;

    //去Es进行检索
    @Override
    public SearchResult getList(SearchParam searchParam) {
    //1，动态构建除查询需要的DSL语句
        SearchResult searchResult = null;
        //准备检索请求
        SearchRequest searchRequest = buildSearchRequrest(searchParam);
        try{
            //2，执行检索
            System.out.println("buildSearchRequrest"+searchRequest.toString());
            SearchResponse search = client.search(searchRequest, SMmallElasticSearchConfig.COMMON_OPTIONS);
            //3，分析响应数据封装成我们需要的数据
            System.out.println(search);
            searchResult = buildSearchResult(search,searchParam);
        }catch (Exception e){
            e.printStackTrace();
        }

        return searchResult;
    }

    /**
     * 准备检索请求，
     * 模糊查询，过滤（按照属性，分类，品牌，价格区间，库存） ，排序，分页，高亮，聚合分析
     * @return
     */
    private SearchRequest buildSearchRequrest(SearchParam searchParam) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        /**
         * 模糊查询,过滤（按照属性，分类，品牌，价格区间，库存）
         */
        //创建一个bool查询对象
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();


        //1.1must-模糊查询
        if(!StringUtils.isEmpty(searchParam.getKeyword())){
            boolQuery.must(QueryBuilders.matchQuery("skuTitle",searchParam.getKeyword()));
        }
        System.out.println(boolQuery);
        //1.2 bool-filter -按照三级分类查询
        if (searchParam.getCatalog3Id() != null){
            boolQuery.filter(QueryBuilders.termQuery("catalogId",searchParam.getCatalog3Id()));
        }
        //1.2 bool-filter -按照品牌Id查询
        if (searchParam.getBrandId() != null && searchParam.getBrandId().size()>0){
            boolQuery.filter(QueryBuilders.termsQuery("brandId",searchParam.getBrandId()));
        }
        //1.2 bool=filter - 按照所有指定属性进行查询
        //attr=1_5寸：6寸
        if (searchParam.getAttrs() != null && searchParam.getAttrs().size() >0){
            for (String attr : searchParam.getAttrs()) {
                BoolQueryBuilder nestedQueryBuilderd = QueryBuilders.boolQuery();
                String[] s = attr.split("_");
                String attrid = s[0];//检索的Id
                String[] strings = s[1].split(":");
                nestedQueryBuilderd.must(QueryBuilders.termsQuery("attrs.attrId",attrid));
                nestedQueryBuilderd.must(QueryBuilders.termsQuery("attrs.attrValue",strings));
                //每一个必须生成一个nested查询
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedQueryBuilderd, ScoreMode.None);
                boolQuery.filter(nestedQuery);
            }
        }
        //1.2 bool - filter -按照库存是否有进行查询
        if (searchParam.getHasStock() != null) {
            boolQuery.filter(QueryBuilders.termQuery("hasStock", searchParam.getHasStock() == 1));
        }

        //1.2 bool-filter - 按照价格区间查询
        if (!StringUtils.isEmpty(searchParam.getSkuPrice())){
            //skuPrice=1_500/500_/_500,gte/lte
            RangeQueryBuilder rangequery = QueryBuilders.rangeQuery("skuPrice");
            String[] s = searchParam.getSkuPrice().split("_");
            if (s.length == 2){
                rangequery.gte(s[0]).lte(s[1]);

            }
            else {
                if (searchParam.getSkuPrice().startsWith("_")){
                    rangequery.gte("0").lte(s[1]);
                }
                if (searchParam.getSkuPrice().endsWith("_")){
                    rangequery.gte(s[1]);
                }
            }
            boolQuery.filter(rangequery);
        }
        searchSourceBuilder.query(boolQuery);

        /**
         *排序，分页，高亮，
         * /**
         *      * sort=saleCount_asc/desc
         *      * sort=skuPrice_asc/desc
         *      * sort=hotScore_asc/desc
         *      */

        //排序
        if (!StringUtils.isEmpty(searchParam.getSort())){
            String sort = searchParam.getSort();
            String[] s = sort.split("_");
          SortOrder order = s[1].equalsIgnoreCase("asc")? SortOrder.ASC:SortOrder.DESC;
            searchSourceBuilder.sort(s[0],order);
        }
        //分页
        searchSourceBuilder.from((searchParam.getPageNumber()-1)*ESConstant.PRODUCT_PAGE_SIZE);
        searchSourceBuilder.size(ESConstant.PRODUCT_PAGE_SIZE);
        //高光
        if (!StringUtils.isEmpty(searchParam.getKeyword())){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style = 'color :red'>");
            highlightBuilder.postTags("</b>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }

        /**
         * 聚合分析
         */
        //品牌聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId").size(50);
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImage.keyword").size(1));
        searchSourceBuilder.aggregation(brand_agg);
//        System.out.println(brand_agg);
        //分类聚合
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(20);
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName.keyword").size(1));
        searchSourceBuilder.aggregation(catalog_agg);
        //属性聚合
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        attr_agg.subAggregation(attr_id_agg);
        attr_id_agg.subAggregation( AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_Value_agg").field("attrs.attrValue").size(50));

        searchSourceBuilder.aggregation(attr_agg);

        String s = searchSourceBuilder.toString();
        System.out.println(s);

        SearchRequest searchRequest = new SearchRequest(new String[]{ESConstant.PRODUCT_INDEX_NAME}, searchSourceBuilder);
        return searchRequest;
    }


    /**
     *   将
     * @param response
     * @param param
     * @return
     */
    private SearchResult buildSearchResult(SearchResponse response,SearchParam param) {
        SearchResult result = new SearchResult();
        //将命中的数据封装到对象返回
        SearchHits hits = response.getHits();
        List<SkuEsModel> models = new ArrayList<>();
        try{
            if (hits.getHits() != null && hits.getHits().length>0){
                for (SearchHit hit : hits.getHits()) {
                    String sourceAsString = hit.getSourceAsString();
                    SkuEsModel esModel = JSONObject.parseObject(sourceAsString, SkuEsModel.class);
                    if (!StringUtils.isEmpty(param.getKeyword())){
                        HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                        String string = skuTitle.getFragments()[0].string();
                        esModel.setSkuTitle(string);
                    }

                    models.add(esModel);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        result.setProducts(models);

        //2.当前所有商品涉及到的所有属性信息
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedNested attr_agg = response.getAggregations().get("attr_agg");
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
        System.out.println("attr_id_agg"+attr_id_agg);
        for (Terms.Bucket bucket : attr_id_agg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            //得到属性ID
            attrVo.setAttrId(bucket.getKeyAsNumber().longValue());
            //得到属性名称
            String attr_name_agg = ((ParsedStringTerms) bucket.
                    getAggregations().get("attr_name_agg")).getBuckets().get(0).getKeyAsString();
            //获取属性的所有值
            List<String> attrValues = ((ParsedStringTerms) bucket.getAggregations().get("attr_Value_agg"))
                    .getBuckets().stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).
                            collect(Collectors.toList());
            attrVo.setAttrName(attr_name_agg);
            attrVo.setAttrValue(attrValues);
            attrVos.add(attrVo);
        }
        result.setAttrs(attrVos);
        //3,当前所有商品涉及到的所有品牌信息
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brand_agg = response.getAggregations().get("brand_agg");
        for (Terms.Bucket bucket : brand_agg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            //1，获取品牌id
            long brandId = bucket.getKeyAsNumber().longValue();
            //获取品牌名字
            String brand_name = ((ParsedStringTerms) bucket.getAggregations().get("brand_name_agg")).getBuckets().get(0).getKeyAsString();
            //获取品牌图片
            String brand_img = ((ParsedStringTerms) bucket.getAggregations().get("brand_img_agg")).getBuckets().get(0).getKeyAsString();
            brandVo.setBrandId(brandId);
            brandVo.setBrandImg(brand_img);
            brandVo.setBrandName(brand_name);
            brandVos.add(brandVo);
        }
        result.setBrands(brandVos);

        //4，设置分类的聚合信息
        ParsedLongTerms catalog_agg = response.getAggregations().get("catalog_agg");
        List<SearchResult.CatelogVo> catelogVos = new ArrayList<>();
        List<? extends Terms.Bucket> buckets = catalog_agg.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            SearchResult.CatelogVo catelogVo = new SearchResult.CatelogVo();
            String catalogIdString = bucket.getKeyAsString();
            catelogVo.setCatalogId(Long.parseLong(catalogIdString));
            String catalog_name_agg =  ((ParsedStringTerms) bucket.getAggregations().get("catalog_name_agg")).getBuckets().get(0).getKeyAsString();

            catelogVo.setCatalogName(catalog_name_agg);
            catelogVos.add(catelogVo);
        }
        result.setCatelogs(catelogVos);

        result.setPageNumber(param.getPageNumber());
        long total = hits.getTotalHits().value;
        result.setTotal(total);

        int totalPages = total % ESConstant.PRODUCT_PAGE_SIZE == 0 ?
                (int) total / ESConstant.PRODUCT_PAGE_SIZE : (int) total / ESConstant.PRODUCT_PAGE_SIZE + 1;
        result.setTotalPages(totalPages);
        List<Integer> pageNav = new ArrayList<>();
        for (int i = 1;i<totalPages;i++){
            pageNav.add(i);
        }
        result.setPageNavs(pageNav);
        System.out.println(result.toString());

        return result;
    }

}
