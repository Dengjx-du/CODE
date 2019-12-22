package com.leyou.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyou.item.entity.TbCategoryBrand;

/**
 * <p>
 * 商品分类和品牌的中间表，两者是多对多关系 Mapper 接口
 * </p>
 *
 * @author HM
 * @since 2019-11-29
 */
public interface TbCategoryBrandMapper extends BaseMapper<TbCategoryBrand> {

}
