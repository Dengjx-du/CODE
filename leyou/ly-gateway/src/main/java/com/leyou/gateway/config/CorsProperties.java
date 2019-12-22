package com.leyou.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * cors的java配置类
 */
@Data
@ConfigurationProperties(prefix = "ly.cors")
public class CorsProperties {
    private List<String> allowedOrigins;//- http://manage.leyou.com
    private Boolean allowedCredentials;//: true
    private List<String> allowedHeaders;
    private List<String> allowedMethods;
    private Long maxAge;//: 3600
    private String filterPath;//: "/**"
}


