package com.leyou.item.controller;

import com.leyou.common.utils.BeanHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.item.DTO.BrandDTO;
import com.leyou.item.entity.TbBrand;
import com.leyou.item.service.TbBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Autowired
    private TbBrandService brandService;

    /**
     * 分页查询品牌信息
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    @GetMapping("/page")
    public ResponseEntity<PageResult<BrandDTO>> findBrandByPage(@RequestParam(name = "key",required = false)String key,
                                                                @RequestParam(name = "page",defaultValue = "1")Integer page,
                                                                @RequestParam(name = "rows",defaultValue = "10")Integer rows,
                                                                @RequestParam(name = "sortBy",required = false)String sortBy,
                                                                @RequestParam(name = "desc",defaultValue = "false")Boolean desc){

        return ResponseEntity.ok(brandService.findBrandByPage(key,page,rows,sortBy,desc));
    }

    /**
     * 保存品牌
     * @param tbBrand
     * @param cids
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveBrand(TbBrand tbBrand,@RequestParam(name = "cids") List<Long> cids){
        brandService.saveBrand(tbBrand,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据分类id 查询品牌的集合
     * @param categoryId
     * @return
     */
    @GetMapping("/of/category")
    public ResponseEntity<List<BrandDTO>> findBrandListByCategoryId(@RequestParam(name = "id")Long categoryId){
        return ResponseEntity.ok(brandService.findBrandListByCategoryId(categoryId));
    }

    /**
     * 根据ids 查询品牌集合
     * @param ids
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<BrandDTO>> findBrandListByIds(@RequestParam(name = "ids")List<Long> ids){
        return ResponseEntity.ok(brandService.findBrandListByIds(ids));
    }

    /**
     * 根据id 查询品牌信息
     * @param brandId
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<BrandDTO> findBrandById(@PathVariable(name = "id")Long brandId){
        TbBrand tbBrand = brandService.getById(brandId);
        BrandDTO brandDTO = BeanHelper.copyProperties(tbBrand, BrandDTO.class);
        return ResponseEntity.ok(brandDTO);
    }

}
