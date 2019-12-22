package com.leyou.search.repository;

import com.leyou.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 这个接口继承ElasticsearchRepository
 * 实现对es简单的CRUD
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
