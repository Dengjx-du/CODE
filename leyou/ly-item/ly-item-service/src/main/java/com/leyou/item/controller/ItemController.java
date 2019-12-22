package com.leyou.item.controller;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.entity.Item;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/item")
public class ItemController {

    @PostMapping("/save")
    public ResponseEntity<Item> saveItem(Item item){

        if(item.getPrice() == null){
//            如果为空 就不能保存
            throw new LyException(ExceptionEnum.PRICE_NOT_NULL);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Item(null,"价格不能为空！",null));
        }
        Item item1 = new Item(1, item.getName(), item.getPrice());
        return ResponseEntity.ok(item1);
    }
}
