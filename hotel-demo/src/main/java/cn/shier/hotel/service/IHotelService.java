package cn.shier.hotel.service;

import cn.shier.hotel.pojo.Hotel;
import cn.shier.hotel.pojo.PageResult;
import cn.shier.hotel.pojo.RequestParams;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface IHotelService extends IService<Hotel> {
    PageResult search(RequestParams params);

    /**
     * 聚合搜索
     */
    Map<String , List<String >> filters(RequestParams params);

    List<String> getSuggestions(String prefix);

    void insertById(Long id);

    void deleteById(Long id);
}
