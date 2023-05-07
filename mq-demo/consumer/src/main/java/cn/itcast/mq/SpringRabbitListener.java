package cn.itcast.mq;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class SpringRabbitListener {
    //@RabbitListener(queues = "Test.queue")
    //public void listenSimpleQueueMessage(String msg) throws InterruptedException {
    //    System.out.println("spring 消费者接收到消息 ：【" + msg + "】");
    //}

    /**
     * 消费者1
     *
     * @param msg
     * @throws InterruptedException
     */
    @RabbitListener(queues = "Test.queue")
    public void listenWorkQueueMessage1(String msg) throws InterruptedException {
        System.out.println("消费者1接收到消息 ：【" + msg + "】" + LocalTime.now());
        Thread.sleep(20);
    }

    /**
     * 消费者2
     *
     * @param msg
     * @throws InterruptedException
     */
    @RabbitListener(queues = "Test.queue")
    public void listenWorkQueueMessage2(String msg) throws InterruptedException {
        System.err.println("消费者2接收到消息 ：【" + msg + "】" + LocalTime.now());
        Thread.sleep(200);
    }


    /**
     * fanout消费者2
     *
     * @param msg
     * @throws InterruptedException
     */
    @RabbitListener(queues = "fanout.queue1")
    public void listenFonoutQueueMessage(String msg) throws InterruptedException {
        System.err.println("消费者接收到fanout.queue1到消息 ：【" + msg + "】");
        Thread.sleep(200);
    }

    /**
     * 消费者2
     *
     * @param msg
     * @throws InterruptedException
     */
    @RabbitListener(queues = "fanout.queue2")
    public void listenFonoutQueueMessage2(String msg) throws InterruptedException {
        System.err.println("消费者接收到fonout.queue2到消息 ：【" + msg + "】");
        Thread.sleep(200);
    }

    /**
     * direct
     *
     * @param msg
     * @throws InterruptedException
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue1"),
            exchange = @Exchange(name = "itcast.direct",type = ExchangeTypes.DIRECT),
            key = {"red", "blue"}))
    public void listenDirectQueueMessage1(String msg) throws InterruptedException {
        System.err.println("消费者接收到direct.queue1到消息 ：【" + msg + "】");
        Thread.sleep(200);
    }

    /**
     * direct
     *
     * @param msg
     * @throws InterruptedException
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue2"),
            exchange = @Exchange(name = "itcast.direct",type = ExchangeTypes.DIRECT),
            key = {"red", "yellow"}))
    public void listenDirectQueueMessage2(String msg) throws InterruptedException {
        System.err.println("消费者接收到direct.queue2到消息 ：【" + msg + "】");
    }

    /**
     * topic
     * @param msg
     * @throws InterruptedException
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "topic.queue1"),
            exchange = @Exchange(name = "itcast.topic",type = ExchangeTypes.TOPIC),
            key = {"china.#"}))
    public void listenTopicQueueMessage1(String msg) throws InterruptedException {
        System.err.println("消费者接收到topic.queue1到消息 ：【" + msg + "】");
    }

    /**
     * topic
     * @param msg
     * @throws InterruptedException
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "topic.queue2"),
            exchange = @Exchange(name = "itcast.topic",type = ExchangeTypes.TOPIC),
            key = {"#.news"}))
    public void listenTopicQueueMessage2(String msg) throws InterruptedException {
        System.err.println("消费者接收到topic.queue2到消息 ：【" + msg + "】");
    }
}
