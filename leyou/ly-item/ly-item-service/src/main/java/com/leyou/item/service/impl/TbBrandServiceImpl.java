package com.leyou.item.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.item.DTO.BrandDTO;
import com.leyou.item.entity.TbBrand;
import com.leyou.item.entity.TbCategoryBrand;
import com.leyou.item.mapper.TbBrandMapper;
import com.leyou.item.service.TbBrandService;
import com.leyou.item.service.TbCategoryBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 品牌表，一个品牌下有多个商品（spu），一对多关系 服务实现类
 * </p>
 *
 * @author HM
 * @since 2019-11-29
 */
@Service
public class TbBrandServiceImpl extends ServiceImpl<TbBrandMapper, TbBrand> implements TbBrandService {

    /**
     * 分页查询品牌信息
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    @Override
    public PageResult<BrandDTO> findBrandByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
//        构造分页对象
        IPage<TbBrand> brandPage = new Page<>(page,rows);
//        构造查询条件
//        select * from tb_brand where name like '%key%' or letter like '%key%' order by sortBy desc/asc
        QueryWrapper<TbBrand> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(key)){
            queryWrapper.lambda().like(TbBrand::getName,key).or() .like(TbBrand::getLetter,key);
        }
        if(!StringUtils.isEmpty(sortBy)){
            if(desc){
                //倒叙
                queryWrapper.orderByDesc(sortBy);
            }else{
//                asc
                queryWrapper.orderByAsc(sortBy);
            }
        }
//        分页查询，结果包含 集合、总条数，总页数
        IPage<TbBrand> brandIPage = this.page(brandPage, queryWrapper);
//          获取返回的集合
        List<TbBrand> tbBrandList = brandIPage.getRecords();
        if(CollectionUtils.isEmpty(tbBrandList)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        List<BrandDTO> brandDTOList = BeanHelper.copyWithCollection(tbBrandList, BrandDTO.class);
        return new PageResult<>(brandDTOList,brandIPage.getTotal(),brandIPage.getPages());
    }

    @Autowired
    private TbCategoryBrandService categoryBrandService;
    /**
     * 保存品牌
     * 处理2张  tb_brand  tb_category_brand
     * @param tbBrand
     * @param cids
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBrand(TbBrand tbBrand, List<Long> cids) {
//        1、保存tb_brand
        boolean b = this.save(tbBrand);
        if(!b){
           throw  new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        Long brandId = tbBrand.getId();
//        2、保存中间表
        List<TbCategoryBrand> tbCategoryBrandList = new ArrayList<>();
//        构造tbCategoryBrandList的值
        for (Long cid : cids) {
            TbCategoryBrand tbCategoryBrand = new TbCategoryBrand();
            tbCategoryBrand.setCategoryId(cid);
            tbCategoryBrand.setBrandId(brandId);
            tbCategoryBrandList.add(tbCategoryBrand);
        }
        boolean b1 = categoryBrandService.saveBatch(tbCategoryBrandList);
        if(!b1){
            throw  new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }
    /**
     * 根据分类id 查询品牌的集合
     * 分类表和 品牌表  是   多对多的关系
     * @param categoryId
     * @return
     */
    @Override
    public List<BrandDTO> findBrandListByCategoryId(Long categoryId) {
        List<TbBrand> brandList = this.getBaseMapper().selectBrandListByCategoryId(categoryId);
        if(CollectionUtils.isEmpty(brandList)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(brandList,BrandDTO.class);
    }
    /**
     * 根据ids 查询品牌集合
     * @param ids
     * @return
     */
    @Override
    public List<BrandDTO> findBrandListByIds(List<Long> ids) {
        Collection<TbBrand> tbBrandCollection = this.listByIds(ids);
        List<TbBrand> tbBrandList = (List<TbBrand>)tbBrandCollection;
        if(CollectionUtils.isEmpty(tbBrandList)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }

        return BeanHelper.copyWithCollection(tbBrandList,BrandDTO.class);
    }
}
