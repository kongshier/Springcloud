package cn.shier.hotel.pojo;

import lombok.Data;

import java.util.List;

/**
 * @author Shier
 * CreateTime 2023/4/24 19:16
 */
@Data
public class PageResult {
    private Long total;
    private List<HotelDoc> hotels;

    public PageResult() {
    }

    public PageResult(Long total, List<HotelDoc> hotels) {
        this.total = total;
        this.hotels = hotels;
    }

}
