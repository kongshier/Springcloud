package cn.shier.hotel.controller;

import cn.shier.hotel.pojo.PageResult;
import cn.shier.hotel.pojo.RequestParams;
import cn.shier.hotel.service.impl.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author Shier
 * CreateTime 2023/4/24 19:18
 */
@RestController
@RequestMapping("/hotel")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @PostMapping("/list")
    public PageResult search(@RequestBody RequestParams params){
        return hotelService.search(params);
    }

    @PostMapping("/filters")
    public Map<String, List<String>> getFilters(@RequestBody RequestParams params){
        return hotelService.filters(params);
    }
    /**
     * 自动补全
     */
    @GetMapping("suggestion")
    public List<String> getSuggestion(@RequestParam("key") String prefix){
        return hotelService.getSuggestions(prefix);
    }
}
