package com.leyou.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.TbUser;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author HM
 * @since 2019-11-29
 */
public interface TbUserService extends IService<TbUser> {

    Boolean checkData(String data, Integer type);

    void sendCode(String phone);

    void register(TbUser tbUser, String code);

    /**
     * 根据用户名和密码查询用户
     * @param userName
     * @param passWord
     * @return
     */
    UserDTO queryUser(String userName, String passWord);
}
