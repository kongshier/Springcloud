package cn.shier.hotel.config;

import cn.shier.hotel.constants.MqConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Shier
 * CreateTime 2023/4/26 17:06
 *
 */
@Configuration
public class MqConfig {

    /**
     * 交换机
     */
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(MqConstants.HOTEL_EXCHANGE,true,false);
    }

    /**
     * 新增监听队列
     */
    @Bean
    public Queue insertQueue(){
        return new Queue(MqConstants.HOTEL_INSET_QUEUE,true);
    }
    /**
     * 删除监听队列
     */
    @Bean
    public Queue deleteQueue(){
        return new Queue(MqConstants.HOTEL_DELETE_QUEUE,true);
    }
    /**
     * 绑定新增事件
     */
    @Bean
    public Binding insertQueueBinding(){
        return BindingBuilder.bind(insertQueue()).to(topicExchange()).with(MqConstants.HOTEL_INSERT_KEY);
    }
    /**
     * 绑定删除事件
     */
    @Bean
    public Binding deleteQueueBinding(){
        return BindingBuilder.bind(deleteQueue()).to(topicExchange()).with(MqConstants.HOTEL_DELETE_KEY);
    }
}
