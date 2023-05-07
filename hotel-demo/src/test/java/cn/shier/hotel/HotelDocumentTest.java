package cn.shier.hotel;

import cn.shier.hotel.pojo.Hotel;
import cn.shier.hotel.pojo.HotelDoc;
import cn.shier.hotel.service.IHotelService;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
class HotelDocumentTest {

    private RestHighLevelClient restClient;

    @Autowired
    private IHotelService hotelService;

    /**
     * 批量添加
     */
    @Test
    void testBulkRequest() throws IOException {
        // 批量查询数据
        List<Hotel> hotels = hotelService.list();

        // request
        BulkRequest request = new BulkRequest();
        // 准备参数,转换为文档类型HotelDoc
        for (Hotel hotel : hotels) {
            HotelDoc hotelDoc = new HotelDoc(hotel);
            request.add(new IndexRequest("shier_hotel")
                    .id(hotelDoc.getId().toString())
                    .source(JSON.toJSONString(hotelDoc), XContentType.JSON));
        }
        // 发送请求
        restClient.bulk(request, RequestOptions.DEFAULT);
    }

    /**
     * 添加数据到文档
     */
    @Test
    void testAddDocument() throws IOException {
        // 获取酒店id 数据
        Hotel hotel = hotelService.getById(60398L);
        // 转换为文档类型
        HotelDoc hotelDoc = new HotelDoc(hotel);
        //Request请求
        IndexRequest request = new IndexRequest("shier_hotel").id(hotel.getId().toString());
        // JSON文档
        request.source(JSON.toJSONString(hotelDoc), XContentType.JSON);
        // 发送请求
        restClient.index(request, RequestOptions.DEFAULT);
    }

    /**
     * 查询文档数据
     */
    @Test
    void testGetDocument() throws IOException {
        // 获取响应
        GetRequest request = new GetRequest("shier_hotel", "60398");
        // 发送请求，得到响应
        GetResponse response = restClient.get(request, RequestOptions.DEFAULT);
        // 解析响应结果
        String json = response.getSourceAsString();
        // 反序列化为HotelDoc对象
        HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
        System.out.println(hotelDoc);
    }

    /**
     * 修改文档
     */
    @Test
    void testUpdateDocument() throws IOException {
        // 获取响应
        UpdateRequest request = new UpdateRequest("shier_hotel", "60398");
        // 发送请求，得到响应
        request.doc(
                "price", "8989",
                "starName", "六星级"
        );
        UpdateResponse update = restClient.update(request, RequestOptions.DEFAULT);
        System.out.println(update);
    }

    /**
     * 删除文档
     */
    @Test
    void testDeleteDocument() throws IOException {
        // 获取响应
        DeleteRequest request = new DeleteRequest("shier-hotel", "60398");
        // 发送请求，得到响应
        restClient.delete(request, RequestOptions.DEFAULT);
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