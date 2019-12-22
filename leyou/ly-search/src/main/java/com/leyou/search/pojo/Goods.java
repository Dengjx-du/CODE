package com.leyou.search.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Map;
import java.util.Set;

/**
 * 一个goods对象 对应 es中的 一条 文档
 */
@Data
@Document(indexName = "goods",type = "docs",shards = 1,replicas = 1)
public class Goods {
//    spuid ,es中的唯一标识id 是字符串类型
    @Id
    @Field(type = FieldType.Keyword)
    private Long id;
//    促销信息, 不分词 ，不创建索引
    @Field(type = FieldType.Keyword,index = false)
    private String subTitle;
//    当前spu下的sku集合，json字符串
    @Field(type = FieldType.Keyword,index = false)
    private String skus;
//    存放用来做全文检索的内容  name + brandname +categoryname
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String all;
    private Long brandId;
    private Long categoryId;
    private Set<Long> price;
    private Long createTime;
//    规格参数 名字和值的对应
    private Map<String,Object> specs;

}
