package cn.shier.hotel;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static cn.shier.hotel.constants.HotelConstants.MAPPING_TEMPLATE;

/**
 * @author Shier
 * CreateTime 2023/4/22 17:41
 */

public class HotelIndexTest {
    private RestHighLevelClient restClient;

    @Test
    void textInit() {
        System.out.println(restClient);
    }


    /**
     * 创建索引库
     *
     * @throws IOException
     */
    @Test
    void testCreateHotelIndex() throws IOException {
        // 1.创建Request对象
        CreateIndexRequest request = new CreateIndexRequest("shier_hotel");
        // 2.请求参数，MAPPING_TEMPLATE是静态常量字符串，内容是创建索引库的DSL语句
        request.source(MAPPING_TEMPLATE, XContentType.JSON);
        // 3.发起请求
        restClient.indices().create(request, RequestOptions.DEFAULT);
    }

    /**
     * 删除索引库
     */
    @Test
    void testDeleteHotelIndex() throws IOException {
        // 1.创建Request对象
        DeleteIndexRequest request = new DeleteIndexRequest("shier_hotel");
        // 2.发起请求
        restClient.indices().delete(request, RequestOptions.DEFAULT);
    }

    /**
     * 判断索引库是否存在
     */
    @Test
    void testExistHotelIndex() throws IOException {
        // 1.创建Request对象
        GetIndexRequest request = new GetIndexRequest("shier_hotel");
        // 2.发起请求
        boolean exists = restClient.indices().exists(request, RequestOptions.DEFAULT);
        // 输出
        System.out.println(exists ? "索引库存在" : "索引库不存在!");
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
