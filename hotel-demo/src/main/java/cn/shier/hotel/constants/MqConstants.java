package cn.shier.hotel.constants;

/**
 * @author Shier
 * CreateTime 2023/4/26 16:56
 * 交换机声明
 */
public class MqConstants {
    /**
     * 交换机
     */
    public static final String HOTEL_EXCHANGE ="hotel.topic";
    /**
     * 监听新增和修改的队列
     */
    public static final String HOTEL_INSET_QUEUE ="hotel.insert_queue";
    /**
     * 监听删除的队列
     */
    public static final String HOTEL_DELETE_QUEUE ="hotel.delete_queue";
    /**
     * 新增或修改的RoutingKey
     */
    public static final String HOTEL_INSERT_KEY ="hotel.insert_key";
    /**
     * 删除的RoutingKey
     */
    public static final String HOTEL_DELETE_KEY ="hotel.delete_key";

}
