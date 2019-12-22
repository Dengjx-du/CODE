package com.leyou.cart.controller;

import com.leyou.cart.dto.CartDTO;
import com.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CartController {

    @Autowired
    private CartService cartService;


    /**
     * 保存购物车数据
     * @param cartDTO
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody CartDTO cartDTO){
//      获取用户信息
        cartService.addCart(cartDTO);
        return ResponseEntity.noContent().build();
    }

    /**
     * 查询用户的所有购物车数据
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<CartDTO>> findCartList(){
        return ResponseEntity.ok(cartService.findCartList());
    }

    /**
     * 修改购物车商品数量
     * @param skuId
     * @param num
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateCart(@RequestParam(name = "id")Long skuId,
                                           @RequestParam(name = "num")Integer num){
        cartService.updateCartNum(skuId,num);
        return ResponseEntity.noContent().build();
    }

    /**
     * 删除购物车信息
     * @param skuId
     * @return
     */
    @DeleteMapping("{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable(name = "skuId")Long skuId){
        cartService.deleteCart(skuId);
        return ResponseEntity.noContent().build();
    }


    /**
     * 用户登录后的购物车合并
     * @param cartDTOS
     * @return
     */
    @PostMapping("/list")
    public ResponseEntity<Void> addCartList(@RequestBody List<CartDTO> cartDTOS){
        cartService.addCartList(cartDTOS);
        return ResponseEntity.noContent().build();
    }
}
