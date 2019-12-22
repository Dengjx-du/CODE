package com.leyou.cart.service;

import com.leyou.cart.dto.CartDTO;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.thradlocals.UserHolder;
import com.leyou.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private String PRE_KEY = "ly:cart:uid:";
    /**
     * 保存购物车数据
     * @param cartDTO
     * @return
     */
    public void addCart(CartDTO cartDTO) {
//      获取用户id
        Long userId = UserHolder.getUser();
//        获取redis中的购物车数据
        String redisKey = PRE_KEY + userId;
        BoundHashOperations<String, String, String> boundHashOps = redisTemplate.boundHashOps(redisKey);
//        获取sku的id
        String hashKey = cartDTO.getSkuId().toString();
//        判断当前的sku是否存在购物车
        Boolean b = boundHashOps.hasKey(hashKey);
        Integer num = cartDTO.getNum();
        if(b != null && b){

//        需要把数量叠加
//            购物车数据的json
            String cartJson = boundHashOps.get(hashKey);
            cartDTO = JsonUtils.toBean(cartJson, CartDTO.class);
            cartDTO.setNum(cartDTO.getNum() + num);
        }
//        直接存放
        boundHashOps.put(hashKey,JsonUtils.toString(cartDTO));
    }


    /**
     * 查询用户的所有购物车数据
     * @return
     */
    public List<CartDTO> findCartList() {
//      获取用户
        Long userId = UserHolder.getUser();
//        判断是否存在用户数据
        String redisKey = PRE_KEY+userId;
        Boolean b = redisTemplate.hasKey(redisKey);
        if(b == null || !b){
            throw  new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
        BoundHashOperations<String, String, String> boundHashOps = redisTemplate.boundHashOps(redisKey);
        //用户所有购物车的数据，json字符串
        List<String> values = boundHashOps.values();
        if(CollectionUtils.isEmpty(values)){
            throw  new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
//        转数据类型 json - > cartDTO
        List<CartDTO> cartDTOList = values.stream().map(cartJson -> {
            return JsonUtils.toBean(cartJson, CartDTO.class);
        }).collect(Collectors.toList());
        return cartDTOList;
    }

    /**
     * 修改购物车商品数量
     * @param skuId
     * @param num
     * @return
     */
    public void updateCartNum(Long skuId, Integer num) {
        Long userId = UserHolder.getUser();
        String redisKey = PRE_KEY + userId;
        Boolean b = redisTemplate.hasKey(redisKey);
        if(b == null || !b){
            throw  new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
        BoundHashOperations<String, String, String> boundHashOps = redisTemplate.boundHashOps(redisKey);
//        获取sku对应的购物车信息
        String hashKey = skuId.toString();
        String cartJson = boundHashOps.get(hashKey);
        if(StringUtils.isEmpty(cartJson)){
            throw  new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
        CartDTO cartDTO = JsonUtils.toBean(cartJson, CartDTO.class);
        cartDTO.setNum(num);
        boundHashOps.put(hashKey,JsonUtils.toString(cartDTO));
    }

    /**
     * 删除购物车信息
     * @param skuId
     * @return
     */
    public void deleteCart(Long skuId) {
        Long userId = UserHolder.getUser();
        String redisKey = PRE_KEY + userId;
        BoundHashOperations<String, String, String> boundHashOps = redisTemplate.boundHashOps(redisKey);
        boundHashOps.delete(skuId.toString());
    }

    /**
     * 用户登录后的购物车合并
     * @param cartDTOS
     * @return
     */
    public void addCartList(List<CartDTO> cartDTOS) {
        Long userId = UserHolder.getUser();
        String redisKey = PRE_KEY + userId;
        Boolean b = redisTemplate.hasKey(redisKey);
        if(b == null || !b){
            return;
        }
        BoundHashOperations<String, String, String> boundHashOps = redisTemplate.boundHashOps(redisKey);
//      循环前端传过来的数据
        for (CartDTO cartDTO : cartDTOS) {
            //skuid
            String hashKey = cartDTO.getSkuId().toString();
//            获取sku的购物车信息
            String cartJson = boundHashOps.get(hashKey);
//            前端购物车内商品的数量
            Integer num = cartDTO.getNum();
            if(!StringUtils.isEmpty(cartJson)){
//                redis中已经存在这个sku的购物车，叠加数量
                cartDTO = JsonUtils.toBean(cartJson, CartDTO.class);
                cartDTO.setNum(num + cartDTO.getNum());
            }
            boundHashOps.put(hashKey,JsonUtils.toString(cartDTO));
        }
    }
}
