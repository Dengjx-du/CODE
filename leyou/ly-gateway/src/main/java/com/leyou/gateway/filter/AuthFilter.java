package com.leyou.gateway.filter;

import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.gateway.config.AllowPathsProperties;
import com.leyou.gateway.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.FORM_BODY_WRAPPER_FILTER_ORDER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

@Slf4j
@Component
public class AuthFilter extends ZuulFilter {

    @Autowired
    private JwtProperties prop;
    @Autowired
    private AllowPathsProperties filterProp;
    /**
     * 类型
     * @return
     */
    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    /**
     * 顺序
     * 数字越小 优先级越高
     * @return
     */
    @Override
    public int filterOrder() {
        return FORM_BODY_WRAPPER_FILTER_ORDER - 1;
    }

    /**
     * 是否过滤
     * 判断白名单，如果在白名单，不需要过滤
     * 如果不在白名单，需要过滤
     * @return
     */
    @Override
    public boolean shouldFilter() {
        return !isAllowPath();
    }

    /**
     * 判断是否 属于 不需要过滤的请求
     * @return  true - 不需要过滤  fase -需要过滤
     */
    private boolean isAllowPath(){
//        获取当前请求上下文
        RequestContext ctx = RequestContext.getCurrentContext();
//        获取request
        HttpServletRequest request = ctx.getRequest();
//        获取到本次请求的url
        String path = request.getRequestURI();
        log.info("本次请求的路径，path={}",path);
//        到白名单来判断
        List<String> allowPaths = filterProp.getAllowPaths();
        for (String allowPath : allowPaths) {
            if(path.startsWith(allowPath)){
                return true;
            }
        }
        return false;
    }
    /**
     * 过滤业务
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        //        获取当前请求上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        //        获取request
        HttpServletRequest request = ctx.getRequest();
        try{
//        获取token
            String token = CookieUtils.getCookieValue(request, prop.getUser().getCookieName());
//        公钥解密token
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, prop.getPublicKey(), UserInfo.class);
//        如果成功，获取用户信息
            UserInfo userInfo = payload.getUserInfo();
//        获取用户角色
            String role = userInfo.getRole();
            String method = request.getMethod();
            String path = request.getRequestURI();
            // TODO 判断权限，此处暂时空置，等待权限服务完成后补充
            log.info("【网关】用户{},角色{}。访问服务{} : {}，", userInfo.getUsername(), role, method, path);
        }catch(Exception e){
//        如果失败 提示重新登录
            e.printStackTrace();
            log.error("非法访问，未登录，地址：{}", request.getRemoteHost(), e );
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }

        return null;
    }
}
