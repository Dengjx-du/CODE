package com.leyou.order.interceptors;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * feign的拦截器
 */
@Slf4j
@Component
public class FeignInterceptor implements RequestInterceptor {
    /**
     * 把前端传过来的request中的cookie拿出来
     * 放在请求user的request头中
     * @param requestTemplate
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
//        获取 新的请求的url
        String path = requestTemplate.path();
//        如果请求不是以/address开头，后面业务也不用做了
        if(!path.startsWith("/address")){
            return;
        }
//        获取请求的上下文
        ServletRequestAttributes servletRequestAttributes=
                (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
//        获取前端传过来的request
        HttpServletRequest request = servletRequestAttributes.getRequest();
//        获取前端传过来的头信息
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()){
            String headerName = headerNames.nextElement();
            //        从头中找到cookie
            if(headerName.equals("cookie")){
                String value = request.getHeader(headerName);
                log.info("cookie的内容是：{}",value);
                //        把cookie放入requestTemplate
                requestTemplate.header(headerName,value);
            }
        }


    }
}
