package com.leyou;

import com.leyou.gateway.config.AllowPathsProperties;
import com.leyou.gateway.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringCloudApplication
@EnableZuulProxy //是增强
@EnableConfigurationProperties({JwtProperties.class, AllowPathsProperties.class})
public class LyGateWay {
    public static void main(String[] args) {
        SpringApplication.run(LyGateWay.class,args);
    }
}
