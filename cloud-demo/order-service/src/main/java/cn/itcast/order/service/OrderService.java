package cn.itcast.order.service;

import cn.itcast.order.mapper.OrderMapper;
import cn.itcast.order.pojo.Order;
import com.shier.feign.clients.UserClient;
import com.shier.feign.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserClient userClient;

    public Order queryOrderById(Long orderId) {
        // 1.查询订单
        Order order = orderMapper.findById(orderId);
        // fegin远程调用
        User user = userClient.findById(order.getUserId());
        // 封装user
        order.setUser(user);
        // 4.返回
        return order;
    }

    //@Autowired
    //private RestTemplate restTemplate;
    //public Order queryOrderById(Long orderId) {
    //    // 1.查询订单
    //    Order order = orderMapper.findById(orderId);
    //    // 2利用RestTemplate发送Http请求，查询用户
    //    // 2.1 url的地址为user-service模块启动的端口，因为是向user-service发送请求
    //    String url = "http://userserver/user/" + order.getUserId();
    //    // 发送请求，完成远程调用
    //    User user = restTemplate.getForObject(url, User.class);
    //    // 封装user
    //    order.setUser(user);
    //    // 4.返回
    //    return order;
    //}
}
