package com.leyou.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leyou.item.entity.TbBrand;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 品牌表，一个品牌下有多个商品（spu），一对多关系 Mapper 接口
 * </p>
 *
 * @author HM
 * @since 2019-11-29
 */
public interface TbBrandMapper extends BaseMapper<TbBrand> {

    List<TbBrand> findBrandList();

    /**
     * 根据分类id 查询品牌的集合
     * 分类表和 品牌表  是   多对多的关系
     * @param categoryId
     * @return
     */
    @Select("SELECT b.id,b.name,b.`letter`,b.`image` FROM tb_category_brand a,tb_brand b\n" +
            "WHERE a.`category_id` = #{cid} AND b.id = a.`brand_id`")
    List<TbBrand> selectBrandListByCategoryId(@Param("cid") Long categoryId);
}
