package com.leyou.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.item.DTO.CategoryDTO;
import com.leyou.item.entity.TbCategory;

import java.util.List;

/**
 * <p>
 * 商品类目表，类目和商品(spu)是一对多关系，类目与品牌是多对多关系 服务类
 * </p>
 *
 * @author HM
 * @since 2019-11-29
 */
public interface TbCategoryService extends IService<TbCategory> {

    List<CategoryDTO> findCategoryByPid(Long parentId);
    /**
     * 根据id 集合查询分类集合
     * @param ids
     * @return
     */
    List<CategoryDTO> findCategoryListByIds(List<Long> ids);
}
