package cn.shier.hotel;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@MapperScan("cn.shier.hotel.mapper")
@SpringBootApplication
public class HotelDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelDemoApplication.class, args);
    }

    /**
     * 注入RestHighLevelClient
     * @return
     */
    @Bean
    public RestHighLevelClient restClient (){
        return new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://8.134.37.7:9200")
        ));
    }

}
