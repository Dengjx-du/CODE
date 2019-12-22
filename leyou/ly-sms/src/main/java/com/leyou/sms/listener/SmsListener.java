package com.leyou.sms.listener;

import com.leyou.common.contants.MQConstants;
import com.leyou.common.utils.JsonUtils;
import com.leyou.sms.config.SmsProperties;
import com.leyou.sms.utils.SmsHelper;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.leyou.common.contants.MQConstants.Exchange.SMS_EXCHANGE_NAME;
import static com.leyou.common.contants.MQConstants.Queue.SMS_VERIFY_CODE_QUEUE;
import static com.leyou.common.contants.MQConstants.RoutingKey.VERIFY_CODE_KEY;

/**
 * 短信消息的监听
 */
@Component
public class SmsListener {

    @Autowired
    private SmsHelper smsHelper;
    @Autowired
    private SmsProperties prop;
    /**
     * 消费消息
     * 短信接口需要参数  "{"code":"123456"}"
     * @param msg  {"phone":"123123123","code":"123456"}
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = SMS_VERIFY_CODE_QUEUE,durable = "true"),
            exchange = @Exchange(value = SMS_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
            key = {VERIFY_CODE_KEY}
    ))
    public void sendMsg(Map<String,String> msg){
//        使用工具类 发送短信
        String phone = msg.remove("phone");
        smsHelper.sendMessage(phone,
                prop.getSignName(),
                prop.getVerifyCodeTemplate(),
                JsonUtils.toString(msg));
    }
}
