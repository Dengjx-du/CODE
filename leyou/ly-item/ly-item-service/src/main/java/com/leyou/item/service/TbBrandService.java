package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.common.vo.PageResult;
import com.leyou.item.DTO.BrandDTO;
import com.leyou.item.entity.TbBrand;

import java.util.List;

/**
 * <p>
 * 品牌表，一个品牌下有多个商品（spu），一对多关系 服务类
 * </p>
 *
 * @author HM
 * @since 2019-11-29
 */
public interface TbBrandService extends IService<TbBrand> {

    PageResult<BrandDTO> findBrandByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc);

    /**
     * 保存品牌
     * @param tbBrand
     * @param cids
     * @return
     */
    void saveBrand(TbBrand tbBrand, List<Long> cids);

    /**
     * 根据分类id 查询品牌的集合
     * @param categoryId
     * @return
     */
    List<BrandDTO> findBrandListByCategoryId(Long categoryId);
    /**
     * 根据ids 查询品牌集合
     * @param ids
     * @return
     */
    List<BrandDTO> findBrandListByIds(List<Long> ids);
}
