package com.leyou.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leyou.common.contants.MQConstants;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.RegexUtils;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.TbUser;
import com.leyou.user.mapper.TbUserMapper;
import com.leyou.user.service.TbUserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.leyou.common.contants.MQConstants.Exchange.SMS_EXCHANGE_NAME;
import static com.leyou.common.contants.MQConstants.RoutingKey.VERIFY_CODE_KEY;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author HM
 * @since 2019-11-29
 */
@Service
public class TbUserServiceImpl extends ServiceImpl<TbUserMapper, TbUser> implements TbUserService {

    /**
     * 检查数据
     * @param data
     * @param type
     * @return
     */
    @Override
    public Boolean checkData(String data, Integer type) {

        QueryWrapper<TbUser> queryWrapper = new QueryWrapper<>();
        switch (type){
            case 1:
                queryWrapper.lambda().eq(TbUser::getUsername,data);
                break;
            case 2:
                queryWrapper.lambda().eq(TbUser::getPhone,data);
                break;
            default:
                throw  new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        int count = this.count(queryWrapper);
        return count==0;
    }

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private AmqpTemplate amqpTemplate;
//  redis中key的前缀
    String PRE_KEY = "ly:user:verify:phone:";
    /**
     * 发送短信
     * @param phone
     */
    @Override
    public void sendCode(String phone) {
//      验证手机号是否正确
        if(!RegexUtils.isPhone(phone)){
            throw  new LyException(ExceptionEnum.INVALID_PHONE_NUMBER);
        }
//      验证手机号是否可用
        QueryWrapper<TbUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbUser::getPhone,phone);
        int count = this.count(queryWrapper);
        if(count>0){
            throw  new LyException(ExceptionEnum.INVALID_PHONE_NUMBER);
        }
//        生成验证码
        String code = RandomStringUtils.randomNumeric(4);
//        放入redis
        String key = PRE_KEY+phone;
        redisTemplate.opsForValue().set(key,code,5, TimeUnit.MINUTES);
        Map<String,String> msg = new HashMap<>();
        msg.put("phone",phone);
        msg.put("code",code);
//        发送消息，rabbitmq的消息
        amqpTemplate.convertAndSend(SMS_EXCHANGE_NAME,
                VERIFY_CODE_KEY,
                msg);
    }

    @Autowired
    private BCryptPasswordEncoder encoder;
    /**
     * 注册
     * @param tbUser
     * @param code
     * @return
     */

    @Override
    public void register(TbUser tbUser, String code) {
//        验证数据是否可用
//        从redis中获取code
        String cacheCode = redisTemplate.opsForValue().get(PRE_KEY + tbUser.getPhone());
//        验证code是否可用
        if(!StringUtils.equals(cacheCode,code)){
            throw new LyException(ExceptionEnum.INVALID_VERIFY_CODE);
        }
//        把用户密码加密,Spring 提供过的 BcryptPasswordEncode，盐是随机的
//        会把随机盐放入加密后的串中
        String encodePwd = encoder.encode(tbUser.getPassword());
        tbUser.setPassword(encodePwd);
//        写入数据库
        boolean b = this.save(tbUser);
        if(!b){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    /**
     * 根据用户名和密码查询用户
     * @param userName
     * @param passWord
     * @return
     */
    @Override
    public UserDTO queryUser(String userName, String passWord) {
        QueryWrapper<TbUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TbUser::getUsername,userName);
        TbUser tbUser = this.getOne(queryWrapper);
        if(tbUser == null){
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        String encodePassword = tbUser.getPassword();
//        验证密码是否正确
        boolean b = encoder.matches(passWord, encodePassword);
        if(!b){
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
        return BeanHelper.copyProperties(tbUser,UserDTO.class);
    }
}
