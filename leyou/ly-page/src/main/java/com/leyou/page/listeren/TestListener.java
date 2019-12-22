package com.leyou.page.listeren;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 测试消息 监听
 */
@Component
public class TestListener {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "testQueue",durable = "true"),
            exchange = @Exchange(value = "demoexchange",type = ExchangeTypes.TOPIC),
            key = {"test.#"}
    ))
    public void getMsg(String msg){
        System.out.println(msg);
    }
}
