package com.leyou.search.listener;

import com.leyou.common.contants.MQConstants;
import com.leyou.item.DTO.SpuDTO;
import com.leyou.item.client.ItemClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.leyou.common.contants.MQConstants.Exchange.ITEM_EXCHANGE_NAME;
import static com.leyou.common.contants.MQConstants.Queue.SEARCH_ITEM_DOWN;
import static com.leyou.common.contants.MQConstants.Queue.SEARCH_ITEM_UP;
import static com.leyou.common.contants.MQConstants.RoutingKey.ITEM_DOWN_KEY;
import static com.leyou.common.contants.MQConstants.RoutingKey.ITEM_UP_KEY;

/**
 * 商品上下架的消息监听
 */
@Component
public class ItemListener {

    @Autowired
    private SearchService searchService;
    @Autowired
    private ItemClient itemClient;
    @Autowired
    private GoodsRepository repository;
    /**
     * 商品上架
     * 添加索引
     * @param spuId
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = SEARCH_ITEM_UP,durable = "true"),
            exchange = @Exchange(value = ITEM_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
            key = {ITEM_UP_KEY}
    ))
    public void itemUp(Long spuId){
        SpuDTO spuDTO = itemClient.findSpuById(spuId);
//        构造goods对象
        Goods goods = searchService.createGoods(spuDTO);
//        操作 es库
        repository.save(goods);
    }

    /**
     * 商品上架
     * 删除索引
     * @param spuId
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = SEARCH_ITEM_DOWN,durable = "true"),
            exchange = @Exchange(value = ITEM_EXCHANGE_NAME,type = ExchangeTypes.TOPIC),
            key = {ITEM_DOWN_KEY}
    ))
    public void itemDown(Long spuId){
        repository.deleteById(spuId);
    }
}
