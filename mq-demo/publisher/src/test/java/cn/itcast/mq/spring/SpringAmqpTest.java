package cn.itcast.mq.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringAmqpTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSimpleQueue() {
        String queueName = "Test.queue";
        String message = "你好啊";
        rabbitTemplate.convertAndSend(queueName, message);
    }

    /**
     * 发送多个消息
     *
     * @throws InterruptedException
     */
    @Test
    public void testWorkQueue() throws InterruptedException {
        String queueName = "Test.queue";
        String message = "你好啊WorkQueue";
        for (int i = 0; i < 50; i++) {
            rabbitTemplate.convertAndSend(queueName, message + i);
            Thread.sleep(30);
        }
    }

    @Test
    public void testFanoutExchange() {
        // 队列名称
        String exchangeName = "itcast.fanout";
        // 消息
        String message = "hello, everyone!";
        rabbitTemplate.convertAndSend(exchangeName, "", message);
    }

    @Test
    public void testDirectExchange() {
        // 队列名称
        String exchangeName = "itcast.direct";
        // 消息
        String message = "hello, red!";
        rabbitTemplate.convertAndSend(exchangeName, "red", message);
    }

    @Test
    public void testTopicExchange() {
        // 队列名称
        String exchangeName = "itcast.topic";
        // 消息
        String message = "中国的消息";
        rabbitTemplate.convertAndSend(exchangeName, "china.news", message);
    }

    /**
     * 消息类型
     */
    @Test
    public void testSendObjectQueue() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", "shier");
        hashMap.put("age", 18);
        rabbitTemplate.convertAndSend("object.queue", hashMap);
    }

    @Test
    public void testSendMap() throws InterruptedException {
        // 准备消息
        Map<String,Object> msg = new HashMap<>();
        msg.put("name", "Jack");
        msg.put("age", 21);
        // 发送消息
        rabbitTemplate.convertAndSend("simple.queue","", msg);
    }
}
