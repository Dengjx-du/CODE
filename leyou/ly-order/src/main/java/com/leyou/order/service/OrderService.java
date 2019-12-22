package com.leyou.order.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.thradlocals.UserHolder;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.IdWorker;
import com.leyou.item.DTO.SkuDTO;
import com.leyou.item.client.ItemClient;
import com.leyou.order.dto.Cart;
import com.leyou.order.dto.OrderDTO;
import com.leyou.order.dto.OrderVO;
import com.leyou.order.entity.TbOrder;
import com.leyou.order.entity.TbOrderDetail;
import com.leyou.order.entity.TbOrderLogistics;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.user.client.UserClient;
import com.leyou.user.dto.AddressDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private ItemClient itemClient;
    @Autowired
    private UserClient userClient;
    @Autowired
    private TbOrderService tbOrderService;
    @Autowired
    private TbOrderDetailService tbOrderDetailService;
    @Autowired
    private TbOrderLogisticsService tbOrderLogisticsService;
    /**
     * 创建订单
     * @param orderDTO
     * @return 订单号
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(OrderDTO orderDTO) {

//        获取用户uid
        Long userId = UserHolder.getUser();
//        生成订单号,使用雪花算法 计算分布式环境下唯一重复的id
        long orderId = idWorker.nextId();
//        获取订单对应的商品列表
        List<Cart> cartList = orderDTO.getCarts();
//        构造map结构，key - skuId ，value - num
        Map<Long, Integer> skuIdNumMap = cartList.stream().collect(Collectors.toMap(Cart::getSkuId, Cart::getNum));
//        获取商品id的集合
        List<Long> skuIds = cartList.stream().map(Cart::getSkuId).collect(Collectors.toList());
//        远程调用item服务，获取sku的集合
        List<SkuDTO> skuDTOList = itemClient.findSkuListByIds(skuIds);
//        订单详情表集合
        List<TbOrderDetail> tbOrderDetailList = new ArrayList<>();
//        计算订单总价
        long totalFee=0L;
        for (SkuDTO skuDTO : skuDTOList) {
            Long skuId = skuDTO.getId();
            Long price = skuDTO.getPrice();
//            获取数量
            Integer num = skuIdNumMap.get(skuId);
//            计算总价
            totalFee += (price.longValue() * num.intValue());
            TbOrderDetail orderDetail = new TbOrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNum(num);
            orderDetail.setSkuId(skuId);
            orderDetail.setPrice(price);
            orderDetail.setTitle(skuDTO.getTitle());
            orderDetail.setOwnSpec(skuDTO.getOwnSpec());
            orderDetail.setImage(StringUtils.substringBefore(skuDTO.getImages(),","));
            tbOrderDetailList.add(orderDetail);
        }
//        计算运费
        long postFee = 0L;
//        计算优惠
        long youhuiFee = 0L;
//        计算实付金额
        long actualFee = totalFee + postFee -youhuiFee;
//        保存订单表
        TbOrder tbOrder = new TbOrder();
        tbOrder.setUserId(userId);
        tbOrder.setOrderId(orderId);
        tbOrder.setTotalFee(totalFee);
        tbOrder.setPostFee(postFee);
        tbOrder.setActualFee(actualFee);
        tbOrder.setSourceType(2);
        tbOrder.setStatus(OrderStatusEnum.INIT.value());
        tbOrder.setPaymentType(orderDTO.getPaymentType());
        boolean b = tbOrderService.save(tbOrder);
        if(!b){
           throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
//        保存订单详情
        boolean b1 = tbOrderDetailService.saveBatch(tbOrderDetailList);
        if(!b1){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
//        从user服务获取用户收货人信息
        AddressDTO addressDTO = userClient.findAddressById(1L);
        if(addressDTO == null){
            throw new LyException(ExceptionEnum.INVALID_ADDRESS);
        }
        TbOrderLogistics tbOrderLogistics = BeanHelper.copyProperties(addressDTO, TbOrderLogistics.class);
        tbOrderLogistics.setOrderId(orderId);
//        保存订单物流
        boolean b2 = tbOrderLogisticsService.save(tbOrderLogistics);
        if(!b2){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        //        减库存，远程调用item微服务,skuId,num
        itemClient.minusStock(skuIdNumMap);
        return orderId;
    }

    /**
     * 根据订单id查询订单信息
     * @param orderId
     * @return
     */
    public OrderVO findOrderById(Long orderId) {
        TbOrder tbOrder = tbOrderService.getById(orderId);
        if(tbOrder == null){
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        return BeanHelper.copyProperties(tbOrder,OrderVO.class);
    }
}
