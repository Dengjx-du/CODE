package com.leyou.upload.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("ly.oss")
public class OssProperties {
    private String accessKeyId;//: LTAIUVK6tAM6xxYm
    private String accessKeySecret;//: L5Y0Lg6r2cAHgm6uVAdkLe5RdzWpRn
    private String host;//: http://leyou-shunyi.oss-cn-beijing.aliyuncs.com # 访问oss的域名，很重要bucket + endpoint
    private String endpoint;//: oss-cn-beijing.aliyuncs.com # 你的服务的端点，不一定跟我一样
    private String dir;//: "" # 保存到bucket的某个子目录
    private Long expireTime;//: 120 # 过期时间，单位是S
    private Long maxFileSize;//: 5242880 #文件大小限制，这里是5M
}
