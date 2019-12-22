package com.leyou.page.listeren;

import com.leyou.common.contants.MQConstants;
import com.leyou.page.service.PageService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.leyou.common.contants.MQConstants.Exchange.ITEM_EXCHANGE_NAME;
import static com.leyou.common.contants.MQConstants.Queue.PAGE_ITEM_DOWN;
import static com.leyou.common.contants.MQConstants.Queue.PAGE_ITEM_UP;
import static com.leyou.common.contants.MQConstants.RoutingKey.ITEM_DOWN_KEY;
import static com.leyou.common.contants.MQConstants.RoutingKey.ITEM_UP_KEY;

/**
 * 商品消息的监听
 */
@Component
public class ItemListener {

    @Autowired
    private PageService pageService;
    /**
     * 上架
     * 创建静态页面
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = PAGE_ITEM_UP,durable = "true"),
            exchange = @Exchange(value = ITEM_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
            key = {ITEM_UP_KEY}
    ))
    public void itemUp(Long spuId){
        pageService.createHtml(spuId);
    }

    /**
     * 下架
     * 删除静态页
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = PAGE_ITEM_DOWN,durable = "true"),
            exchange = @Exchange(value = ITEM_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
            key = {ITEM_DOWN_KEY}
    ))
    public void itemDown(Long spuId){
        pageService.removeHtml(spuId);
    }
}
