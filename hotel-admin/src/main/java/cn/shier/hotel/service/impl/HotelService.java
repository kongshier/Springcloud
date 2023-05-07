package cn.shier.hotel.service.impl;

import cn.shier.hotel.mapper.HotelMapper;
import cn.shier.hotel.pojo.Hotel;
import cn.shier.hotel.service.IHotelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class HotelService extends ServiceImpl<HotelMapper, Hotel> implements IHotelService {
}
