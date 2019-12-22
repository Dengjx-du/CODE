package com.leyou.order.dto;

import lombok.Data;

import java.util.List;

/**
 * 前端传递参数
 */
@Data
public class OrderDTO {

    private Long addressId;
    private Integer paymentType;
    private List<Cart> carts;
}
