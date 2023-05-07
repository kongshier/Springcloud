package cn.shier.hotel.service.impl;

import cn.shier.hotel.mapper.HotelMapper;
import cn.shier.hotel.pojo.Hotel;
import cn.shier.hotel.pojo.HotelDoc;
import cn.shier.hotel.pojo.PageResult;
import cn.shier.hotel.pojo.RequestParams;
import cn.shier.hotel.service.IHotelService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HotelService extends ServiceImpl<HotelMapper, Hotel> implements IHotelService {

    @Autowired
    private RestHighLevelClient restClient;

    /**
     * 搜索
     *
     * @param params
     * @return
     */
    @Override
    public PageResult search(RequestParams params) {
        try {
            // 1.准备Request
            SearchRequest request = new SearchRequest("shier_hotel");
            // 2.准备DSL
            // 2.1.query关键字搜索

            builderBasicQuery(params, request);

            // 2.2.分页
            int size = params.getSize();
            int page = params.getPage();
            request.source().from(page - 1).size(size);

            // 2.3 距离排序
            String location = params.getLocation();
            if (location != null && !location.equals("")) {
                request.source().sort(
                        SortBuilders.
                                geoDistanceSort("location", new GeoPoint(location))
                                .order(SortOrder.ASC)
                                .unit(DistanceUnit.KILOMETERS)
                );
            }
            // 2.4 sortBy排序
            String sortBy = params.getSortBy();
            //System.out.println(sortBy);
            if (sortBy != null && !sortBy.equals("")) {
                if (sortBy.equals("default")) {
                    ;
                }
                if (sortBy.equals("score")) {
                    request.source().sort("score", SortOrder.DESC);
                }
                if (sortBy.equals("price")) {
                    request.source().sort("price", SortOrder.ASC);
                }
            }


            // 3.发送请求
            SearchResponse response = restClient.search(request, RequestOptions.DEFAULT);
            // 4.解析响应
            return handleHighResponse(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 聚合搜索
     *
     * @return
     */
    @Override
    public Map<String, List<String>> filters(RequestParams params) {
        try {
            // 获取request
            SearchRequest request = new SearchRequest("shier_hotel");
            // DSL参数
            request.source().size(0);
            buliderAggregation(request);
            // 查询信息
            builderBasicQuery(params, request);
            // 发送请求
            SearchResponse response = restClient.search(request, RequestOptions.DEFAULT);
            // 解析结果
            Map<String, List<String>> resultMap = new HashMap<>();
            Aggregations aggregations = response.getAggregations();
            // 根据多个名称，获取到聚合结果
            List<String> brandList = getAggByName(aggregations, "brandAgg");
            resultMap.put("品牌", brandList);
            List<String> starList = getAggByName(aggregations, "starAgg");
            resultMap.put("星级", starList);
            List<String> cityList = getAggByName(aggregations, "cityAgg");
            resultMap.put("城市", cityList);
            return resultMap;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 自动补全
     *
     * @param prefix
     * @return
     */
    @Override
    public List<String> getSuggestions(String prefix) {
        try {
            // request请求
            SearchRequest request = new SearchRequest("shier_hotel");
            // DSl 参数
            request.source().suggest(new SuggestBuilder().addSuggestion(
                    "suggestions",
                    SuggestBuilders.completionSuggestion("suggestion")
                            .prefix(prefix)
                            .skipDuplicates(true)
                            .size(10)

            ));
            // 发送请求
            SearchResponse response = restClient.search(request, RequestOptions.DEFAULT);
            Suggest suggest = response.getSuggest();
            CompletionSuggestion suggestions = suggest.getSuggestion("suggestions");
            List<CompletionSuggestion.Entry.Option> options = suggestions.getOptions();
            List<String> list = new ArrayList<>(options.size());
            for (CompletionSuggestion.Entry.Option option : options) {
                String text = option.getText().toString();
                list.add(text);
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 新增酒店
     *
     * @param id
     */
    @Override
    public void insertById(Long id) {
        try {
            // 获取酒店的id
            Hotel hotel = getById(id);
            HotelDoc hotelDoc = new HotelDoc(hotel);
            IndexRequest request = new IndexRequest("shier_hotel").id(hotel.getId().toString());

            request.source(JSON.toJSONString(hotelDoc), XContentType.JSON);

            restClient.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        try {
            DeleteRequest request = new DeleteRequest("shier_hotel", id.toString());
            restClient.delete(request,RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据不同的名称聚合
     *
     * @param aggregations
     * @param aggName
     * @return
     */
    private static List<String> getAggByName(Aggregations aggregations, String aggName) {
        Terms brandTerms = aggregations.get(aggName);
        // 获取桶
        List<? extends Terms.Bucket> buckets = brandTerms.getBuckets();
        List<String> brandList = new ArrayList<>();
        for (Terms.Bucket bucket : buckets) {
            String key = bucket.getKeyAsString();
            brandList.add(key);
        }
        return brandList;
    }

    /**
     * 聚合条件
     *
     * @param request
     */
    private static void buliderAggregation(SearchRequest request) {
        request.source().aggregation(
                AggregationBuilders
                        .terms("brandAgg")
                        .field("brand")
                        .size(200));
        request.source().aggregation(
                AggregationBuilders
                        .terms("cityAgg")
                        .field("city")
                        .size(200));
        request.source().aggregation(
                AggregationBuilders
                        .terms("starAgg")
                        .field("starName")
                        .size(200));
    }


    /**
     * 条件过滤搜索
     *
     * @param params
     * @param request
     */
    private void builderBasicQuery(RequestParams params, SearchRequest request) {
        // 构建BoolQueryBuilder
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 关键字搜索
        String key = params.getKey();
        if (key == null || "".equals(key)) {
            boolQuery.must(QueryBuilders.matchAllQuery());
        } else {
            boolQuery.must(QueryBuilders.matchQuery("all", key));
            // 高亮显示
            request.source().highlighter(new HighlightBuilder().field("name").requireFieldMatch(false));
        }
        // 城市过滤
        if (params.getCity() != null && !params.getCity().equals("")) {
            boolQuery.filter(QueryBuilders.termQuery("city", params.getCity()));
        }
        // 品牌过滤
        if (params.getBrand() != null && !params.getBrand().equals("")) {
            boolQuery.filter(QueryBuilders.termQuery("brand", params.getBrand()));
        }
        // 星级过滤
        if (params.getStarName() != null && !params.getStarName().equals("")) {
            boolQuery.filter(QueryBuilders.termQuery("starName", params.getStarName()));
        }
        //价格过滤
        if (params.getMinPrice() != null && params.getMaxPrice() != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("price")
                    .gte(params.getMinPrice())
                    .lte(params.getMaxPrice()));
        }
        // 算分控制
        FunctionScoreQueryBuilder functionScoreQuery = QueryBuilders.functionScoreQuery(
                boolQuery,
                new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                        new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                QueryBuilders.termQuery("isAD", true),
                                ScoreFunctionBuilders.weightFactorFunction(10)
                        )
                });
        request.source().query(functionScoreQuery);
    }

    /**
     * 高亮显示
     *
     * @param response
     * @return
     */
    public PageResult handleHighResponse(SearchResponse response) {
        // 4.解析响应
        SearchHits searchHits = response.getHits();
        // 4.1.获取总条数
        long total = searchHits.getTotalHits().value;
        System.out.println("共搜索到" + total + "条数据");
        // 4.2.文档数组
        SearchHit[] hits = searchHits.getHits();
        // 4.3.遍历
        List<HotelDoc> hotels = new ArrayList<>();
        for (SearchHit hit : hits) {
            // 获取文档source
            String json = hit.getSourceAsString();
            // 反序列化
            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);

            // 高亮设置
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();

            // 搜索的不能为空
            if (!CollectionUtils.isEmpty(highlightFields)) {
                // 根据字段名获取高亮效果
                HighlightField highlightField = highlightFields.get("name");
                if (highlightField != null && !highlightField.equals("")) {
                    // 获取高亮结果
                    String string = highlightField.getFragments()[0].string();
                    // 覆盖非高亮的结果
                    hotelDoc.setName(string);
                }
            }

            // 获取排序值,显示值
            Object[] sortValues = hit.getSortValues();
            if (sortValues.length > 0) {
                Object value = sortValues[0];
                hotelDoc.setDistance(value);
            }
            hotels.add(hotelDoc);
        }
        // 封装返回
        return new PageResult(total, hotels);
    }


}
