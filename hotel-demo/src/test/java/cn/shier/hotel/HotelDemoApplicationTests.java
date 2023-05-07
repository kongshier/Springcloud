package cn.shier.hotel;

import cn.shier.hotel.service.IHotelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HotelDemoApplicationTests {

    @Autowired
    private IHotelService hotelService;

    @Test
    void contextLoads() {
        //Map<String, List<String>> filters = hotelService.filters();
        //System.out.println(filters);
    }

}
