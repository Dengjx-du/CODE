package com.leyou.cart.interceptors;

import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.thradlocals.UserHolder;
import com.leyou.common.utils.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义拦截器
 */
@Slf4j
public class UserInterceptor  implements HandlerInterceptor {
    /**
     * 获取token
     * 获取用户信息
     * 把用户信息放入ThreadLocal
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try{
//        获取token
            String token = CookieUtils.getCookieValue(request, "LY_TOKEN");
//        获取用户信息
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, UserInfo.class);
//        获取用户id
            Long userId = payload.getUserInfo().getId();
//        把userId放入ThreadLocal
            UserHolder.setUser(userId);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            log.error("获取用户id 失败");
            return false;
        }

    }

    /**
     * 删除Threadlocal中的数据
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
