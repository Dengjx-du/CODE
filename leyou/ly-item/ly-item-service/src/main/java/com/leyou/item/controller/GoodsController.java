package com.leyou.item.controller;

import com.leyou.common.vo.PageResult;
import com.leyou.item.DTO.SkuDTO;
import com.leyou.item.DTO.SpuDTO;
import com.leyou.item.DTO.SpuDetailDTO;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 操作spu 、sku
 */
@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;
    /**
     * 分页查询spu信息
     * @param page
     * @param rows
     * @param key
     * @param saleable
     * @return
     */
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuDTO>> findSpuByPage(@RequestParam(name = "page",defaultValue = "1")Integer page,
                                                            @RequestParam(name = "rows",defaultValue = "5")Integer rows,
                                                            @RequestParam(name = "key",required = false)String key,
                                                            @RequestParam(name = "saleable",required = false)Boolean saleable){
        return ResponseEntity.ok(goodsService.findSpuByPage(page,rows,key,saleable));
    }

    /**
     * 新增商品
     * @param spuDTO
     * @return
     */
    @PostMapping("/goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuDTO spuDTO){
        goodsService.saveGoods(spuDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改商品上下架
     * @param spuId
     * @param saleable
     * @return
     */
    @PutMapping("/spu/saleable")
    public ResponseEntity<Void> updateSaleable(@RequestParam(name = "id")Long spuId,
                                               @RequestParam(name = "saleable")Boolean saleable){
        goodsService.updateSaleable(spuId,saleable);
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据spuid 查询detail
     * @param spuId
     * @return
     */
    @GetMapping("/spu/detail")
    public ResponseEntity<SpuDetailDTO> findSpuDetailBySpuId(@RequestParam(name = "id")Long spuId){
        return ResponseEntity.ok(goodsService.findSpuDetailBySpuId(spuId));
    }

    /**
     * 根据spuid 查询sku集合
     *
     * @param spuId
     * @return
     */
    @GetMapping("/sku/of/spu")
    public ResponseEntity<List<SkuDTO>> findSkuListBySpuId(@RequestParam(name = "id")Long spuId){
        return ResponseEntity.ok(goodsService.findSkuListBySpuId(spuId));
    }

    /**
     * 保存商品
     * @param spuDTO
     * @return
     */
    @PutMapping("/goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuDTO spuDTO){
        goodsService.updateGoods(spuDTO);
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据主键查询 spu信息
     * @param spuId
     * @return
     */
    @GetMapping("/spu/{id}")
    public ResponseEntity<SpuDTO> findSpuById(@PathVariable(name = "id")Long spuId){
        return ResponseEntity.ok(goodsService.findSpuById(spuId));
    }


    /**
     * 根据主键id集合获取 sku集合
     * @param ids
     * @return
     */
    @GetMapping("/sku/list")
    public ResponseEntity<List<SkuDTO>> findSkuListByIds(@RequestParam(name = "ids")List<Long> ids){
        return ResponseEntity.ok(goodsService.findSkuListByIds(ids));
    }

    /**
     * sku商品减库存
     * @param skuIdNumMap
     */
    @PutMapping("/sku/minusStock")
    public ResponseEntity<Void> minusStock(@RequestBody Map<Long, Integer> skuIdNumMap){
        goodsService.minusStock(skuIdNumMap);
        return ResponseEntity.noContent().build();
    }
}
