package com.leyou.upload.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OSSConfig {

    /**
     * 从oss SDK中创建自己的oss对象
     * @param prop
     * @return
     */
    @Bean
    public OSS ossConfig(OssProperties prop){
        return new OSSClientBuilder().build(prop.getEndpoint(),
                prop.getAccessKeyId(),
                prop.getAccessKeySecret());
    }
}
