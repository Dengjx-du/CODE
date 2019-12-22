package com.leyou.order.dto;

import lombok.Data;

/**
 * 购物车商品
 */
@Data
public class Cart {
    private Long skuId;
    private Integer num;
}
