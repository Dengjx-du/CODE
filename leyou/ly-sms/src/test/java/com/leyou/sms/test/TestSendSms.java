package com.leyou.sms.test;

import com.leyou.common.contants.MQConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.junit4.rules.SpringClassRule;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestSendSms {

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 测试短信发送
     * 把消息内容发送到 rabbitmq
     */
    @Test
    public void sendSms(){

        Map<String,String> msg = new HashMap<>();
        msg.put("phone","18247622823");
        msg.put("code","234567");
        amqpTemplate.convertAndSend(MQConstants.Exchange.SMS_EXCHANGE_NAME,
                MQConstants.RoutingKey.VERIFY_CODE_KEY,
                msg);
    }
}
