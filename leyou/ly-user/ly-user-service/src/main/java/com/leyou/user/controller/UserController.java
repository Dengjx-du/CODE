package com.leyou.user.controller;

import com.leyou.common.exception.LyException;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.TbUser;
import com.leyou.user.service.TbUserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

@Api(value = "用户中心的controller")
@RestController
public class UserController {

    @Autowired
    private TbUserService userService;

    /**
     * 检查数据是否可用
     * @param data 要校验的数据
     * @param type 要校验的数据类型：1，用户名；2，手机
     * @return
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkData(@PathVariable(name = "data")String data,
                                             @PathVariable(name = "type")Integer type){
        return ResponseEntity.ok(userService.checkData(data,type));
    }

    /**
     * 发送短信验证码
     * @param phone
     * @return
     */
    @PostMapping("/code")
    public ResponseEntity<Void> sendCode(@RequestParam(name = "phone")String phone){

        userService.sendCode(phone);
        return ResponseEntity.noContent().build();
    }

    /**
     * 注册
     * @param tbUser
     * @param code
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid TbUser tbUser, BindingResult result, @RequestParam(name = "code")String code){
        if(result.hasErrors()){
            String errorMsg = result.getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(","));
            throw new LyException(HttpStatus.BAD_REQUEST.value(),errorMsg);
        }
        userService.register(tbUser,code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据用户名和密码查询用户
     * @param userName
     * @param passWord
     * @return
     */
    @GetMapping("/query")
    public ResponseEntity<UserDTO> queryUser(@RequestParam(name = "username")String userName,
                                             @RequestParam(name = "password")String passWord){
        return ResponseEntity.ok(userService.queryUser(userName,passWord));
    }
}
