package com.leyou.user.interceptors;

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
 * 拦截请求
 * 从token中获取用户id
 */
@Slf4j
public class UserInterceptor implements HandlerInterceptor {
    /**
     * 从token获取用户id
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try{
            String token = CookieUtils.getCookieValue(request, "LY_TOKEN");
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, UserInfo.class);
//            获取用户id
            Long userId = payload.getUserInfo().getId();
//            放入容器
            UserHolder.setUser(userId);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            log.error("获取用户id失败！！");
            return false;
        }
    }

    /**
     * 删除ThreadLocal中的数据
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
