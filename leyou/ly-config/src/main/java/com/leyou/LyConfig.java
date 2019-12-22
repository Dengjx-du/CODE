package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * 配置中心 server
 */
@SpringBootApplication
@EnableConfigServer  //开启配置中心
public class LyConfig {
    public static void main(String[] args) {
        SpringApplication.run(LyConfig.class,args);
    }
}
