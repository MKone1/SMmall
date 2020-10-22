package com.yxl.smmall.search;

import com.alibaba.fastjson.JSON;
import com.yxl.smmall.search.config.SMmallElasticSearchConfig;
import com.yxl.smmall.search.vo.SearchResult;
import lombok.Data;
import lombok.ToString;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Map;

@SpringBootTest
class SmmallSearchApplicationTests {
    @Autowired
    private RestHighLevelClient esRestClient;

    @Test
    void contextLoads() {
        SearchResult searchResult = null;
        //准备检索请求
//        SearchRequest searchRequest = buildSearchRequrest(searchParam);
//        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
//        brand_agg.field("brandId").size(50);
//        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandN ame").size(1));
//        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
//        searchSourceBuilder.aggregation(brand_agg);
    }





    /**
     * 测试存储数据到ES中,更新
     */
    @Test
    public void indexData() throws IOException {
        //public IndexRequest(String index){},指定一个索引
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("2");
        //1,方法一
//        indexRequest.source("username","zhangsan","age",18,"gender","man");
        //2,方法二
        User user = new User();
        user.setAge(18);
        user.setName("xiaoyu");
        user.setGender("F");
        String jsonString = JSON.toJSONString(user);
        indexRequest.source(jsonString, XContentType.JSON);
//XContentType.JSON就是传入的JSON 的数据类型
        //同步调用
        // IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
        //异步调用
        // client.indexAsync(request, RequestOptions.DEFAULT, listener);

        //执行保存操作
        IndexResponse index = esRestClient.index(indexRequest, SMmallElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(index);
    }

    @Data
    class User {
        private String name;
        private Integer age;
        private String gender;
    }
/**
 * 更多的增删改查见官方文档
 */

    /**
     * 创建检索请求
     * 对于检索，可以查看官方文档：https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.x/java-rest-high-search.html
     * 来参考
     */
    @Test
    public void searchData() throws IOException {
        //1,创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        //指定索引
        searchRequest.indices("newbank");
        //指定DSL，检索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //1.1 检索条件
//        searchSourceBuilder.query();
//        searchSourceBuilder.from();
//        searchSourceBuilder.size();
//        searchSourceBuilder.aggregation();
        searchSourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
        //按年龄进行聚合
        TermsAggregationBuilder size = AggregationBuilders.terms("ageaggs").field("age").size(10);
        searchSourceBuilder.aggregation(size);
        //求年龄的平均值
        AvgAggregationBuilder ageavg = AggregationBuilders.avg("ageavg").field("age");
        //求薪资的平均值
        AvgAggregationBuilder balanceavg = AggregationBuilders.avg("balanceavg").field("balance");
        searchSourceBuilder.aggregation(balanceavg);

        System.out.println("条件" + searchSourceBuilder.toString());
        searchRequest.source(searchSourceBuilder);

//        SearchRequest searchRequest = new SearchRequest();
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
//        searchRequest.source(searchSourceBuilder);


        //2，执行检索
        /**
         * 同步执行：SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
         * 异步执行：client.searchAsync(searchRequest, RequestOptions.DEFAULT, listener);
         */
        SearchResponse searchResponse = esRestClient.search(searchRequest, SMmallElasticSearchConfig.COMMON_OPTIONS);
        //打印结果
        System.out.println(searchResponse.toString());
        //将结果封装成一个MAP,
        //   Map map = JSON.parseObject(search.toString(), Map.class);

        //得到相关检索信息
        /**
         *RestStatus status = searchResponse.status();
         * TimeValue took = searchResponse.getTook();
         * Boolean terminatedEarly = searchResponse.isTerminatedEarly();
         * boolean timedOut = searchResponse.isTimedOut()
         *
         * int totalShards = searchResponse.getTotalShards();
         * int successfulShards = searchResponse.getSuccessfulShards();
         * int failedShards = searchResponse.getFailedShards();
         * for (ShardSearchFailure failure : searchResponse.getShardFailures()) {
         *     // failures should be handled here
         * }
         *
         *
         */
//        3.1 分析检索出来的参数，简单来说，return的数据外部有个hits，其中还有一个hits数组；
        SearchHits hits = searchResponse.getHits();
        TotalHits totalHits = hits.getTotalHits();
// the total number of hits, must be interpreted in the context of totalHits.relation
        long numHits = totalHits.value;
// whether the number of hits is accurate (EQUAL_TO) or a lower bound of the total (GREATER_THAN_OR_EQUAL_TO)
        TotalHits.Relation relation = totalHits.relation;
        float maxScore = hits.getMaxScore();
        System.out.println("numHits"+numHits);//获取数据数量
        System.out.println("maxScore"+maxScore); //获取最大得分
        //获取具体数据
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits){
            /**
             *  "_index" : "bank",
             *         "_type" : "account",
             *         "_id" : "1",
             *         "_score" : 1.0,
             */
//            String index = hit.getIndex();
//            String id = hit.getId();
//            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Account account = JSON.parseObject(sourceAsString, Account.class);
            System.out.println("account"+account);

        }

        //3.2 获取聚合操作的数据
        Aggregations aggregations = searchResponse.getAggregations();
        Terms ageaggs = aggregations.get("ageaggs");
        for (Terms.Bucket bucket:ageaggs.getBuckets()){
            String keyAsString = bucket.getKeyAsString();
            System.out.println("年龄"+keyAsString);
        }
        Avg balanceavg1 = aggregations.get("balanceavg");
        System.out.println("平均薪资"+balanceavg1.getValue());
    }
    @Data
    @ToString
   static class Account{
            private int account_number;
            private int balance;
            private String firstname;
            private String lastname;
            private int age;
            private String gender;
            private String address;
            private String employer;
            private String email;
            private String city;
            private String state;
    }
    /**
     * {
     *      1，
     *      sku在ES中的数据模型：
     *      sku索引：{
     *          skuId:1
     *          spuId:111
     *          xxx
     *      }
     *      attr索引：{
     *          spuId:11
     *          attrs:{
     *              {尺寸
     *
     *              ，，，，，，}
     *          }
     *      }
     *      可以检索SKU索引，检索到spuId，根据spuId 在attr索引中查询其他信息；
     *
     *      PS：在这种情况下，
     *
     * }
     *
     */
}
