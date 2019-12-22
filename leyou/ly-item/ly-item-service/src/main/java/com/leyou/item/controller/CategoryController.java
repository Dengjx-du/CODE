package com.leyou.item.controller;

import com.leyou.item.DTO.CategoryDTO;
import com.leyou.item.service.TbCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private TbCategoryService categoryService;
    /**
     * 根据父id 查询子分类的集合
     * @param parentId
     * @return
     */
    @GetMapping("/of/parent")
    public ResponseEntity<List<CategoryDTO>> findCategoryByPid(@RequestParam(name = "pid") Long parentId){
        return ResponseEntity.ok(categoryService.findCategoryByPid(parentId));
    }

    /**
     * 根据id 集合查询分类集合
     * @param ids
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<CategoryDTO>> findCategoryListByIds(@RequestParam(name = "ids")List<Long> ids){
        return ResponseEntity.ok(categoryService.findCategoryListByIds(ids));
    }
}
