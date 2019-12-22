package com.leyou.common.test;

import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.auth.utils.RsaUtils;
import io.jsonwebtoken.Jwt;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

public class TestRsa {

    /**
     * 测试rsa工具类
     */
    @Test
    public void testRsa() throws Exception {
        String pubKeyName = "C:\\workspace\\heima-jee109\\ssh\\id_rsa.pub";
        String privateKeyName = "C:\\workspace\\heima-jee109\\ssh\\id_rsa";
        RsaUtils.generateKey(pubKeyName,privateKeyName,"hello world",1024);

        PublicKey publicKey = RsaUtils.getPublicKey(pubKeyName);
        System.out.println(publicKey);

        PrivateKey privateKey = RsaUtils.getPrivateKey(privateKeyName);
        System.out.println(privateKey);
    }

    @Test
    public void testJwt() throws Exception {
        String pubKeyName = "C:\\workspace\\heima-jee109\\ssh\\id_rsa.pub";
        String privateKeyName = "C:\\workspace\\heima-jee109\\ssh\\id_rsa";


        PublicKey publicKey = RsaUtils.getPublicKey(pubKeyName);
        System.out.println(publicKey);

        PrivateKey privateKey = RsaUtils.getPrivateKey(privateKeyName);
        System.out.println(privateKey);

//        构造 用户自描述信息
        UserInfo userInfo = new UserInfo(123L, "tom", "admin");
//        使用私钥生成token
        String token = JwtUtils.generateTokenExpireInSeconds(userInfo, privateKey, 600);
        System.out.println("token=="+token);

//        使用公钥解密token
        Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, publicKey, UserInfo.class);
//        获取载荷中的内容
        String jwtId = payload.getId();
        System.out.println("jwtid=="+jwtId);
        Date expiration = payload.getExpiration();
        System.out.println("过期时间="+expiration);
//        获取用户自描述信息
        UserInfo userInfo1 = payload.getUserInfo();
        System.out.println(userInfo1.getUsername());
    }
}
