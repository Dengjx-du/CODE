package com.leyou.item.controller;

import com.leyou.item.DTO.SpecGroupDTO;
import com.leyou.item.DTO.SpecParamDTO;
import com.leyou.item.entity.TbSpecGroup;
import com.leyou.item.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 处理规格组和规格的操作
 */
@RestController
@RequestMapping("/spec")
public class SpecController {

    @Autowired
    private SpecService specService;

    /**
     * 根据分类id 查询 分组的集合
     * @param categoryId
     * @return
     */
    @GetMapping("/groups/of/category")
    public ResponseEntity<List<SpecGroupDTO>> findGroupListByCategoryId(@RequestParam(name = "id")Long categoryId){

        return ResponseEntity.ok(specService.findGroupListByCategoryId(categoryId));

    }

    /**
     * 保存规格组
     * @param group
     * @return
     */
    @PostMapping("/group")
    public ResponseEntity<Void> saveGroup(@RequestBody TbSpecGroup group){
        specService.saveGroup(group);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    /**
     * 根据条件查询 规格参数的名字
     * @param groupId
     * @return
     */
    @GetMapping("/params")
    public ResponseEntity<List<SpecParamDTO>> findParamList(@RequestParam(name = "gid",required = false)Long groupId,
                                                            @RequestParam(name = "cid",required = false)Long categoryId,
                                                            @RequestParam(name = "searching",required = false)Boolean searching){
        return ResponseEntity.ok(specService.findParamList(groupId,categoryId,searching));
    }

    /**
     * 查询规格参数组，及组内参数
     * @param cid 商品分类id
     * @return 规格组及组内参数
     */
    @GetMapping("/of/category")
    public ResponseEntity<List<SpecGroupDTO>> findSpecsByCid(@RequestParam("id") Long cid){
        return ResponseEntity.ok(specService.findSpecsByCid(cid));
    }
}
