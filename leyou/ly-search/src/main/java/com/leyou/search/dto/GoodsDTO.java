package com.leyou.search.dto;

import lombok.Data;

@Data
public class GoodsDTO {
//    spu的id
    private Long id;
//    促销信息
    private String subTitle;
//    sku的集合json字符串
    private String skus;
}
