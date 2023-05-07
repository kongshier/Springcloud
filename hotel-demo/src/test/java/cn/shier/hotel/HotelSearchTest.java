package cn.shier.hotel;

import cn.shier.hotel.pojo.HotelDoc;
import cn.shier.hotel.service.IHotelService;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@SpringBootTest
class HotelSearchTest {

    private RestHighLevelClient restClient;

    @Autowired
    private IHotelService hotelService;

    /**
     * 自动补全
     */
    @Test
    void  testAutoSuggest() throws IOException {
        // 1. 准备request请求
        SearchRequest request = new SearchRequest("shier_hotel");
        // 2.DSL参数
        request.source().suggest(new SuggestBuilder().addSuggestion(
                "suggestions",
                SuggestBuilders.completionSuggestion("suggestion")
                        .prefix("s")
                        .skipDuplicates(true)
                        .size(10)
        ));
        // 3.发送请求
        SearchResponse response = restClient.search(request, RequestOptions.DEFAULT);

        // 4. 解析结果
        Suggest suggest = response.getSuggest();
        CompletionSuggestion suggestions = suggest.getSuggestion("suggestions");
        List<CompletionSuggestion.Entry.Option> options = suggestions.getOptions();
        for (CompletionSuggestion.Entry.Option option : options) {
            String text = option.getText().toString();
            System.out.println(text);
        }
    }

    /**
     * 聚合数据搜索
     */
    @Test
    void testAggregation() throws IOException {
        // 1. 准备request请求
        SearchRequest request = new SearchRequest("shier_hotel");
        // 2.DSL参数
        request.source().size(0);
        request.source().aggregation(
                AggregationBuilders
                        .terms("brandAgg")
                        .field("brand")
                        .size(20));
        // 3.发送请求
        SearchResponse response = restClient.search(request, RequestOptions.DEFAULT);

        // 4.解析结果
        // 4.1 拿到最外层的aggregation
        Aggregations aggregations = response.getAggregations();
        // 4.2拿到聚合的名称
        Terms brandTerms = aggregations.get("brandAgg");
        // 4.3拿到聚合里面的桶
        List<? extends Terms.Bucket> buckets = brandTerms.getBuckets();
        // 4.4遍历数组获取key
        for (Terms.Bucket bucket : buckets) {
            String brandName = bucket.getKeyAsString();
            System.out.println(brandName);
        }
    }

    @Test
    void testHighlight() throws IOException {
        // 1.准备Request
        SearchRequest request = new SearchRequest("shier_hotel");
        // 2.准备DSL
        // 2.1.query
        request.source().query(QueryBuilders.matchQuery("all", "如家"));
        // 2.2.高亮
        request.source().highlighter(new HighlightBuilder().field("name").requireFieldMatch(false));
        // 3.发送请求
        SearchResponse response = restClient.search(request, RequestOptions.DEFAULT);
        // 4.解析响应
        handleHighResponse(response);

    }

    public void handleHighResponse(SearchResponse response){
        // 4.解析响应
        SearchHits searchHits = response.getHits();
        // 4.1.获取总条数
        long total = searchHits.getTotalHits().value;
        System.out.println("共搜索到" + total + "条数据");
        // 4.2.文档数组
        SearchHit[] hits = searchHits.getHits();
        // 4.3.遍历
        for (SearchHit hit : hits) {
            // 获取文档source
            String json = hit.getSourceAsString();
            // 反序列化
            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
            // 获取高亮结果
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (!CollectionUtils.isEmpty(highlightFields)) {
                // 根据字段名获取高亮结果
                HighlightField highlightField = highlightFields.get("name");
                if (highlightField != null) {
                    // 获取高亮值
                    String name = highlightField.getFragments()[0].string();
                    // 覆盖非高亮结果
                    hotelDoc.setName(name);
                }
            }
            System.out.println("hotelDoc = " + hotelDoc);
        }
    }


    /**
     * 分页、排序
     * @throws IOException
     */
    @Test
    void testPageAndSort() throws IOException {
        // 页码，每页大小
        int page = 1, size = 5;
        // 1.准备Request
        SearchRequest request = new SearchRequest("shier_hotel");
        // 2.准备DSL
        // 2.1.query
        request.source().query(QueryBuilders.matchAllQuery());
        // 2.2.排序 sort
        request.source().sort("price", SortOrder.ASC);
        // 2.3.分页 from、size
        request.source().from((page - 1) * size).size(5);
        // 3.发送请求
        SearchResponse response = restClient.search(request, RequestOptions.DEFAULT);
        // 4.解析响应
        handleResponse(response);
    }

    /**
     * Match_All查询
     * @throws IOException
     */
    @Test
    void testMatchAll() throws IOException {
        // 获取request
        SearchRequest request = new SearchRequest("shier_hotel");
        // dsl语句
        request.source().query(QueryBuilders.matchAllQuery());
        // 发送请求
        SearchResponse response = restClient.search(request, RequestOptions.DEFAULT);

        handleResponse(response);
    }

    /**
     * Match查询
     * @throws IOException
     */
    @Test
    void testMatch() throws IOException {
        // 获取request
        SearchRequest request = new SearchRequest("shier_hotel");
        // dsl语句
        request.source().query(QueryBuilders.matchQuery("all","上海"));
        // 发送请求
        SearchResponse response = restClient.search(request, RequestOptions.DEFAULT);
        // 解析结果
        handleResponse(response);
    }


    /**
     * Term查询
     * @throws IOException
     */
    @Test
    void testTerm() throws IOException {
        // 获取request
        SearchRequest request = new SearchRequest("shier_hotel");
        // dsl语句
        request.source().query(QueryBuilders.termQuery("city","上海"));
        // 发送请求
        SearchResponse response = restClient.search(request, RequestOptions.DEFAULT);
        // 解析结果
        handleResponse(response);
    }
    /**
     * 布尔查询
     * @throws IOException
     */
    @Test
    void testBool() throws IOException {
        // 获取request
        SearchRequest request = new SearchRequest("shier_hotel");
        // 准备BooleanQuery
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 添加term
        boolQuery.must(QueryBuilders.termQuery("city", "杭州"));
        // 添加range
        boolQuery.filter(QueryBuilders.rangeQuery("price").lte(250));

        request.source().query(QueryBuilders.termQuery("city","上海"));
        // 发送请求
        SearchResponse response = restClient.search(request, RequestOptions.DEFAULT);
        // 解析结果
        handleResponse(response);
    }

    private static void handleResponse(SearchResponse response) {
        // 解析结果
        SearchHits searchHits = response.getHits();
        // 查询总条数
        long total = searchHits.getTotalHits().value;
        System.out.println("搜索结果有：" + total + "条");
        // 查询结果是数组
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit searchHit : hits) {
            // 得到source
            String source = searchHit.getSourceAsString();
            // 反序列化
            HotelDoc hotelDoc = JSON.parseObject(source, HotelDoc.class);
            // 打印结果
            System.out.println(hotelDoc);
        }
    }

    // 单元测试之前都会去初始化以下这两个测试
    @BeforeEach
    void setUp() {
        this.restClient = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://8.134.37.7:9200")
        ));
    }

    @AfterEach
    void tearDown() throws IOException {
        this.restClient.close();
    }
}