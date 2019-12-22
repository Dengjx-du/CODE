package com.leyou.item.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leyou.common.contants.MQConstants;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.item.DTO.SkuDTO;
import com.leyou.item.DTO.SpuDTO;
import com.leyou.item.DTO.SpuDetailDTO;
import com.leyou.item.entity.*;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.leyou.common.contants.MQConstants.Exchange.ITEM_EXCHANGE_NAME;
import static com.leyou.common.contants.MQConstants.RoutingKey.ITEM_DOWN_KEY;
import static com.leyou.common.contants.MQConstants.RoutingKey.ITEM_UP_KEY;

@Service
public class GoodsService {

    @Autowired
    private TbSpuService spuService;
    /**
     * 分页查询spu信息
     * @param page
     * @param rows
     * @param key
     * @param saleable
     * @return
     */
    public PageResult<SpuDTO> findSpuByPage(Integer page, Integer rows, String key, Boolean saleable) {

        IPage<TbSpu> page1 = new Page(page,rows);
        QueryWrapper<TbSpu> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(key)){
            queryWrapper.lambda().like(TbSpu::getName,key);
        }
        if(saleable != null){
            queryWrapper.lambda().eq(TbSpu::getSaleable,saleable);
        }
        IPage<TbSpu> spuIPage = spuService.page(page1, queryWrapper);
        List<TbSpu> tbSpuList = spuIPage.getRecords();
        if(CollectionUtils.isEmpty(tbSpuList)){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        List<SpuDTO> spuDTOList = BeanHelper.copyWithCollection(tbSpuList, SpuDTO.class);
        handlerCategoryNameAndBrandName(spuDTOList);
        return new PageResult<>(spuDTOList,spuIPage.getTotal(),spuIPage.getPages());
    }

    @Autowired
    private TbBrandService tbBrandService;
    @Autowired
    private TbCategoryService tbCategoryService;

    private void handlerCategoryNameAndBrandName(List<SpuDTO> spuDTOList) {
        for (SpuDTO spuDTO : spuDTOList) {
            Long brandId = spuDTO.getBrandId();
            TbBrand tbBrand = tbBrandService.getById(brandId);
            spuDTO.setBrandName(tbBrand.getName());

//            处理3级分类的名字
            String cname = "";
            Collection<TbCategory> tbCategoryCollection = tbCategoryService.listByIds(spuDTO.getCategoryIds());
//            for (TbCategory tbCategory : tbCategoryCollection) {
//                if(cname.length()>0){
//                    cname += "/";
//                }
//                cname += tbCategory.getName();
//            }
            cname = tbCategoryCollection.stream().map(TbCategory::getName).collect(Collectors.joining("/"));



            spuDTO.setCategoryName(cname);

        }
    }

    @Autowired
    private TbSpuDetailService tbSpuDetailService;
    @Autowired
    private TbSkuService tbSkuService;
    /**
     * 新增商品
     * @param spuDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveGoods(SpuDTO spuDTO) {
//        1、保存tb_spu
        TbSpu tbSpu = BeanHelper.copyProperties(spuDTO, TbSpu.class);
        boolean b = spuService.save(tbSpu);
        if(!b){
           throw  new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        Long spuId = tbSpu.getId();
//        2、保存tb_detail
        TbSpuDetail tbSpuDetail = BeanHelper.copyProperties(spuDTO.getSpuDetail(), TbSpuDetail.class);
        tbSpuDetail.setSpuId(spuId);
        boolean b1 = tbSpuDetailService.save(tbSpuDetail);
        if(!b1){
            throw  new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
//        3、保存tb_sku
        List<SkuDTO> skuDTOList = spuDTO.getSkus();
        List<TbSku> tbSkuList = BeanHelper.copyWithCollection(skuDTOList, TbSku.class);
        for (TbSku tbSku : tbSkuList) {
            tbSku.setSpuId(spuId);
        }
        boolean b2 = tbSkuService.saveBatch(tbSkuList);
        if(!b2){
            throw  new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    @Autowired
    private AmqpTemplate amqpTemplate;
    /**
     * 修改商品上下架
     * @param spuId
     * @param saleable
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateSaleable(Long spuId, Boolean saleable) {
//        修改 tb_spu
        TbSpu tbSpu = new TbSpu();
        tbSpu.setId(spuId);
        tbSpu.setSaleable(saleable);
        boolean b = spuService.updateById(tbSpu);
        if(!b){
            throw  new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
//        修改 tb_sku
//        update tb_sku set enable =? where spu_id = ?
        UpdateWrapper<TbSku> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(TbSku::getSpuId,spuId);
        updateWrapper.lambda().set(TbSku::getEnable,saleable);
        boolean b1 = tbSkuService.update(updateWrapper);
        if(!b1){
            throw  new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
        String routingKey = saleable? ITEM_UP_KEY:ITEM_DOWN_KEY;
//      发送上下架消息,消息内容是spuId
        amqpTemplate.convertAndSend(ITEM_EXCHANGE_NAME,
                routingKey,
                spuId);
    }

    /**
     * 根据spuid 查询detail
     * @param spuId
     * @return
     */
    public SpuDetailDTO findSpuDetailBySpuId(Long spuId) {
        TbSpuDetail tbSpuDetail = tbSpuDetailService.getById(spuId);
        if(tbSpuDetail == null){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return BeanHelper.copyProperties(tbSpuDetail,SpuDetailDTO.class);
    }

    /**
     * 根据spuid 查询sku集合
     *
     * @param spuId
     * @return
     */
    public List<SkuDTO> findSkuListBySpuId(Long spuId) {
        QueryWrapper<TbSku> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbSku::getSpuId,spuId);
        List<TbSku> tbSkuList = tbSkuService.list(queryWrapper);
        return BeanHelper.copyWithCollection(tbSkuList,SkuDTO.class);
    }

    /**
     * 保存商品
     * @param spuDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateGoods(SpuDTO spuDTO) {
        Long spuId = spuDTO.getId();
//        修改spu
        TbSpu tbSpu = BeanHelper.copyProperties(spuDTO, TbSpu.class);
        boolean b = spuService.updateById(tbSpu);
        if(!b){
            throw  new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
//        修改detail
        SpuDetailDTO spuDetailDTO = spuDTO.getSpuDetail();
        TbSpuDetail tbSpuDetail = BeanHelper.copyProperties(spuDetailDTO, TbSpuDetail.class);
        boolean b1 = tbSpuDetailService.updateById(tbSpuDetail);
        if(!b1){
            throw  new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
//        删除sku
        QueryWrapper<TbSku> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbSku::getSpuId,spuId);
        boolean b2 = tbSkuService.remove(queryWrapper);
        if(!b2){
            throw  new LyException(ExceptionEnum.DELETE_OPERATION_FAIL);
        }
//        新增sku
        List<SkuDTO> skuDTOList = spuDTO.getSkus();
        List<TbSku> tbSkuList = BeanHelper.copyWithCollection(skuDTOList, TbSku.class);
        for (TbSku tbSku : tbSkuList) {
            tbSku.setSpuId(spuId);
        }
        boolean b3 = tbSkuService.saveBatch(tbSkuList);
        if(!b3){
            throw  new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    /**
     * 根据主键查询 spu信息
     * @param spuId
     * @return
     */
    public SpuDTO findSpuById(Long spuId) {
        TbSpu tbSpu = spuService.getById(spuId);
        if(tbSpu== null){
            throw  new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        return BeanHelper.copyProperties(tbSpu,SpuDTO.class);
    }

    /**
     * 根据主键id集合获取 sku集合
     * @param ids
     * @return
     */
    public List<SkuDTO> findSkuListByIds(List<Long> ids) {
        Collection<TbSku> tbSkuCollection = tbSkuService.listByIds(ids);
        if(CollectionUtils.isEmpty(tbSkuCollection)){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        List<SkuDTO> skuDTOList = tbSkuCollection.stream().map(tbSku -> {
            return BeanHelper.copyProperties(tbSku, SkuDTO.class);
        }).collect(Collectors.toList());
        return skuDTOList;
    }

    /**
     * sku商品减库存
     * @param skuIdNumMap
     */
    @Transactional(rollbackFor = Exception.class)
    public void minusStock(Map<Long, Integer> skuIdNumMap) {
//        update tb_sku set stock = stock - ? where id = ?
        for (Long skuId : skuIdNumMap.keySet()) {
            Integer num = skuIdNumMap.get(skuId);
            int code = tbSkuService.minusStock(skuId,num);
            if(code <1 ){
                throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
            }
        }
    }
}
