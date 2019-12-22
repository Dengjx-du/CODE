package com.leyou.user.client;

import com.leyou.user.dto.AddressDTO;
import com.leyou.user.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户服务的feign接口
 */
@FeignClient("user-service")
public interface UserClient {
    /**
     * 根据用户名和密码查询用户
     * @param userName
     * @param passWord
     * @return
     */
    @GetMapping("/query")
    UserDTO queryUser(@RequestParam(name = "username")String userName,
                                             @RequestParam(name = "password")String passWord);

    /**
     * 获取用户的收货人信息
     * @param addressId
     * @return
     */
    @GetMapping("/address/findAddressById")
    AddressDTO findAddressById(@RequestParam(name = "id")Long addressId);
}
