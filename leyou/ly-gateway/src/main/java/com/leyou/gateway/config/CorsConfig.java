package com.leyou.gateway.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableConfigurationProperties(CorsProperties.class)
public class CorsConfig {

    /**
     * 使用spring的corsFilter
     */
    @Bean
    public CorsFilter crosFilter(CorsProperties prop){
//      1、创建corsfilter的配置
        CorsConfiguration config = new CorsConfiguration();
//        配置允许的源
        config.setAllowedOrigins(prop.getAllowedOrigins());
//        配置允许携带cookie
        config.setAllowCredentials(prop.getAllowedCredentials());
//        配置允许的请求类型 ,可以有多个
        config.setAllowedMethods(prop.getAllowedMethods());
//        配置允许的头信息 ,用*来标识所有
        config.setAllowedHeaders(prop.getAllowedHeaders());
//        配置有效期
        config.setMaxAge(prop.getMaxAge());
//      2、拦截所有的请求，并使用上面写好的配置
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(prop.getFilterPath(),config);
//      3、创建一个新的 包含拦截路径和配置的corsFilter
        return new CorsFilter(source);
    }







//    @Bean
//    public CorsFilter crosFilter(){
////      1、创建corsfilter的配置
//        CorsConfiguration config = new CorsConfiguration();
////        配置允许的源
//        config.addAllowedOrigin("http://manage.leyou.com");
////        配置允许携带cookie
//        config.setAllowCredentials(true);
////        配置允许的请求类型 ,可以有多个
//        config.addAllowedMethod("GET");
//        config.addAllowedMethod("POST");
//        config.addAllowedMethod("PUT");
////        配置允许的头信息 ,用*来标识所有
//        config.addAllowedHeader("*");
////        配置有效期
//        config.setMaxAge(3600L);
////      2、拦截所有的请求，并使用上面写好的配置
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**",config);
////      3、创建一个新的 包含拦截路径和配置的corsFilter
//        return new CorsFilter(source);
//    }
}
