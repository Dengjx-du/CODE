package com.leyou.order.controller;

import com.leyou.order.dto.OrderDTO;
import com.leyou.order.dto.OrderVO;
import com.leyou.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    /**
     * 创建订单
     * @param orderDTO
     * @return
     */
    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody OrderDTO orderDTO){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.createOrder(orderDTO));
    }

    /**
     * 根据订单号查询订单信息
     * @param orderId
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderVO> findOrderById(@PathVariable(name = "id")Long orderId){
        return  ResponseEntity.ok(orderService.findOrderById(orderId));
    }
}
