package com.leyou.gateway.config;

import com.leyou.common.auth.utils.RsaUtils;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.PrivateKey;
import java.security.PublicKey;

@Data
@ConfigurationProperties("ly.jwt")
public class JwtProperties implements InitializingBean {
    private String pubKeyPath;

    private PublicKey publicKey;


    private UserTokenProperties user = new UserTokenProperties();

    @Data
    public class UserTokenProperties{
        private String cookieName;//: LY_TOKEN # cookie名称
    }


//    @PostConstruct
//    public void  jwtInit(){
//        System.out.println("pubKeyPath="+pubKeyPath);
//        System.out.println("priKeyPath="+priKeyPath);
//
//        try {
//            publicKey = RsaUtils.getPublicKey(pubKeyPath);
//            privateKey = RsaUtils.getPrivateKey(priKeyPath);
//            System.out.println(publicKey);
//            System.out.println(privateKey);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("pubKeyPath="+pubKeyPath);

        try {
            publicKey = RsaUtils.getPublicKey(pubKeyPath);
            System.out.println(publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
