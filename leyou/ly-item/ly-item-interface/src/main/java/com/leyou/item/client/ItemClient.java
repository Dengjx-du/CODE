package com.leyou.item.client;

import com.leyou.common.vo.PageResult;
import com.leyou.item.DTO.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品微服务的feign接口
 */
@FeignClient("item-service")
public interface ItemClient {

    /**
     * 根据spuid 查询sku集合
     *
     * @param spuId
     * @return
     */
    @GetMapping("/sku/of/spu")
    List<SkuDTO> findSkuListBySpuId(@RequestParam(name = "id") Long spuId);

    /**
     * 根据条件查询 规格参数的名字
     * @param groupId
     * @return
     */
    @GetMapping("/spec/params")
    List<SpecParamDTO> findParamList(@RequestParam(name = "gid",required = false)Long groupId,
                                                            @RequestParam(name = "cid",required = false)Long categoryId,
                                                            @RequestParam(name = "searching",required = false)Boolean searching);

    /**
     * 根据spuid 查询detail
     * @param spuId
     * @return
     */
    @GetMapping("/spu/detail")
    SpuDetailDTO findSpuDetailBySpuId(@RequestParam(name = "id")Long spuId);

    /**
     * 分页查询spu信息
     * @param page
     * @param rows
     * @param key
     * @param saleable
     * @return
     */
    @GetMapping("/spu/page")
    PageResult<SpuDTO> findSpuByPage(@RequestParam(name = "page",defaultValue = "1")Integer page,
                                     @RequestParam(name = "rows",defaultValue = "5")Integer rows,
                                     @RequestParam(name = "key",required = false)String key,
                                     @RequestParam(name = "saleable",required = false)Boolean saleable);

    /**
     * 根据id 集合查询分类集合
     * @param ids
     * @return
     */
    @GetMapping("/category/list")
    List<CategoryDTO> findCategoryListByIds(@RequestParam(name = "ids")List<Long> ids);

    /**
     * 根据ids 查询品牌集合
     * @param ids
     * @return
     */
    @GetMapping("/brand/list")
    List<BrandDTO> findBrandListByIds(@RequestParam(name = "ids")List<Long> ids);

    /**
     * 根据主键查询 spu信息
     * @param spuId
     * @return
     */
    @GetMapping("/spu/{id}")
    SpuDTO findSpuById(@PathVariable(name = "id")Long spuId);

    /**
     * 根据id 查询品牌信息
     * @param brandId
     * @return
     */
    @GetMapping("/brand/{id}")
    BrandDTO findBrandById(@PathVariable(name = "id")Long brandId);

    /**
     * 查询规格参数组，及组内参数
     * @param id 商品分类id
     * @return 规格组及组内参数
     */
    @GetMapping("/spec/of/category")
    List<SpecGroupDTO> findSpecsByCid(@RequestParam("id") Long id);

    /**
     * 根据id集合 查询sku集合
     * @param skuIds
     * @return
     */
    @GetMapping("/sku/list")
    List<SkuDTO> findSkuListByIds(@RequestParam(name = "ids") List<Long> skuIds);

    /**
     * sku商品减库存
     * @param skuIdNumMap
     */
    @PutMapping("/sku/minusStock")
    void minusStock(@RequestBody  Map<Long, Integer> skuIdNumMap);
}
