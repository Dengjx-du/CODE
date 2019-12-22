package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.item.entity.TbSku;

/**
 * <p>
 * sku表,该表表示具体的商品实体,如黑色的 64g的iphone 8 服务类
 * </p>
 *
 * @author HM
 * @since 2019-11-29
 */
public interface TbSkuService extends IService<TbSku> {

    /**
     * 减库存
     * @param skuId
     * @param num
     */
    int minusStock(Long skuId, Integer num);
}
