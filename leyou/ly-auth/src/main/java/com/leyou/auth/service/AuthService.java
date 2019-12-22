package com.leyou.auth.service;

import com.leyou.auth.config.JwtProperties;
import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.auth.utils.RsaUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.CookieUtils;
import com.leyou.user.client.UserClient;
import com.leyou.user.dto.UserDTO;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.PrivateKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    @Autowired
    private UserClient userClient;
    @Autowired
    private JwtProperties prop;
    /**
     * 登录
     * @param userName
     * @param passWord
     * @return
     */
    public void login(String userName, String passWord, HttpServletResponse response) {
//      1、远程调用user服务，验证用户名和密码
        UserDTO userDTO = userClient.queryUser(userName, passWord);
//        2、创建用户的自描述信息
        UserInfo userInfo = new UserInfo(userDTO.getId(), userDTO.getUsername(), "guest");
//        3、把用户的自描述信息放入token，生成token
        try {
//            获取私钥
            PrivateKey privateKey = prop.getPrivateKey();//RsaUtils.getPrivateKey(prop.getPriKeyPath());
//            生成token
            String token = JwtUtils.generateTokenExpireInMinutes(userInfo, privateKey, prop.getUser().getExpire());
//        4、把token发送给客户端
            CookieUtils.newCookieBuilder()
                    .name(prop.getUser().getCookieName())
                    .value(token)
                    .response(response)
                    .domain(prop.getUser().getCookieDomain())
                    .httpOnly(true)  //当前的cookie只允许http携带，不允许js操作，安全
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }


    }

    /**
     * 验证用户是否登录
     * @param request
     * @return
     */
    public UserInfo verify(HttpServletRequest request,HttpServletResponse response) {
        try{
//        获取token
            String token = CookieUtils.getCookieValue(request, prop.getUser().getCookieName());
//        解析token
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, prop.getPublicKey(), UserInfo.class);
//            获取jwt 的id
            String jwtId = payload.getId();
//            在redis判断这个jwtid是否存在
            Boolean b = redisTemplate.hasKey(jwtId);
//            如果在redis中存在，说明这个jwt已经被注销了，提示重新登录
            if(b!=null && b){
                throw new LyException(ExceptionEnum.UNAUTHORIZED);
            }
//        返回数据
            UserInfo userInfo = payload.getUserInfo();
//            获取token的过期时间
            Date expiration = payload.getExpiration();
//          计算刷新的时间点
            DateTime time = new DateTime(expiration).minusMinutes(prop.getUser().getMinRefreshInterval());
//            判断 刷新时间是否小于 当前时间
            if(time.isBeforeNow()){
//                用jwt工具生成token
                token = JwtUtils.generateTokenExpireInMinutes(userInfo, prop.getPrivateKey(), prop.getUser().getExpire());
//                把token放入cookie
                CookieUtils.newCookieBuilder()
                        .name(prop.getUser().getCookieName())
                        .value(token)
                        .domain(prop.getUser().getCookieDomain())
                        .response(response)
                        .httpOnly(true)
                        .build();
            }

            return userInfo;
        }catch(Exception e){
            e.printStackTrace();
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }

    }

    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 退出登录
      * 在redis中存储黑名单
     * @param request
     * @param response
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        try{
//        获取用户token
            String token = CookieUtils.getCookieValue(request, prop.getUser().getCookieName());
//        删除cookie
            CookieUtils.deleteCookie(prop.getUser().getCookieName(),
                    prop.getUser().getCookieDomain(),
                    response);
//        获取payload
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, prop.getPublicKey(), UserInfo.class);
//         获取jwtid
            String jwtId = payload.getId();
//        获取到token的过期时间
            Date expiration = payload.getExpiration();
//        计算redis中的过期时间
            long timeout = expiration.getTime() - System.currentTimeMillis();
//        添加到redis的黑名单中，设置有效期
            redisTemplate.opsForValue().set(jwtId,"1",timeout, TimeUnit.MILLISECONDS);
        }catch(Exception e){
            e.printStackTrace();

        }

    }
}
