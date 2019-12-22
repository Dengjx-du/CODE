package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.DTO.CategoryDTO;
import com.leyou.item.entity.TbCategory;
import com.leyou.item.mapper.TbCategoryMapper;
import com.leyou.item.service.TbCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品类目表，类目和商品(spu)是一对多关系，类目与品牌是多对多关系 服务实现类
 * </p>
 *
 * @author HM
 * @since 2019-11-29
 */
@Service
public class TbCategoryServiceImpl extends ServiceImpl<TbCategoryMapper, TbCategory> implements TbCategoryService {

    /**
     * 根据父id 查询子类集合
     * @param parentId
     * @return
     */
    @Override
    public List<CategoryDTO> findCategoryByPid(Long parentId) {
//      select * from tb_category where parent_id = ?
//        创建用户构造条件的querywrapper
        QueryWrapper<TbCategory> queryWrapper = new QueryWrapper<>();
//        向querywrapper中设置查询条件
//        queryWrapper.eq("parent_id",parentId);
//        面向对象表达式
        queryWrapper.lambda().eq(TbCategory::getParentId,parentId);
//        调用生成的方法 查询数据库
        List<TbCategory> tbCategoryList = this.list(queryWrapper);
        if(CollectionUtils.isEmpty(tbCategoryList)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
//        对象的转换 spring中的Beanutils的方法
        List<CategoryDTO> categoryDTOList = BeanHelper.copyWithCollection(tbCategoryList, CategoryDTO.class);
        return categoryDTOList;
    }

    /**
     * 根据id 集合查询分类集合
     * @param ids
     * @return
     */
    @Override
    public List<CategoryDTO> findCategoryListByIds(List<Long> ids) {
        Collection<TbCategory> tbCategoryCollection = this.listByIds(ids);
//        List<TbCategory> tbCategoryList = (List<TbCategory>)tbCategoryCollection;
//        if(CollectionUtils.isEmpty(tbCategoryList)){
//            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
//        }

        List<CategoryDTO> categoryDTOList = tbCategoryCollection.stream().map(tbcategory -> {
            return BeanHelper.copyProperties(tbcategory, CategoryDTO.class);
        }).collect(Collectors.toList());

        return BeanHelper.copyWithCollection(categoryDTOList,CategoryDTO.class);
    }
}
