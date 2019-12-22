package com.leyou.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

@Data
@Configuration
@ConfigurationProperties("ly.encoder.crypt")
public class PasswordEncode {

    /**
     * 强度 ，加密次数 1-31 ，数越大，加密时间越长
     * 一般不要超过10次
     */
    private Integer strength;
    /**
     * 随机的盐
     */
    private String secret;


    @Bean
    public BCryptPasswordEncoder encoder(){
        SecureRandom random= new SecureRandom(secret.getBytes());
        return new BCryptPasswordEncoder(strength,random);
    }
}
