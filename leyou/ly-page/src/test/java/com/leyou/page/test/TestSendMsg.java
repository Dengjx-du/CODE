package com.leyou.page.test;

import com.leyou.page.service.PageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestSendMsg {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    public void testSendMsg(){
        amqpTemplate.convertAndSend("demoexchange","test.msg","这个amqp的demo");
    }
}
