package cn.shier.hotel.mq;

import cn.shier.hotel.constants.MqConstants;
import cn.shier.hotel.service.IHotelService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Shier
 * CreateTime 2023/4/26 17:25
 */
@Component
public class HotelListener {

    @Autowired
    private IHotelService hotelService;

    /**
     * 监听酒店新增或修改的业务
     * @param id
     */
    @RabbitListener(queues = MqConstants.HOTEL_INSET_QUEUE)
    public void listenHotelInsertOrUpdate(Long id){
        hotelService.insertById(id);
    }
    /**
     * 监听酒店删除的业务
     * @param id
     */
    @RabbitListener(queues = MqConstants.HOTEL_INSET_QUEUE)
    public void listenHotelDelete(Long id){
        hotelService.deleteById(id);
    }
}
