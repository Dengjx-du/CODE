package com.leyou.user.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestRedis {


    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void test(){
        redisTemplate.opsForValue().set("phone","123456789");
        Object phone = redisTemplate.opsForValue().get("phone");
        System.out.println("获取到内容："+phone);
//        Map<String,Map<String,Val>>
        redisTemplate.opsForHash().put("user","name","jack");
        Object name = redisTemplate.opsForHash().get("user", "name");
        System.out.println(name);
        BoundHashOperations<String, String, String> boundHashOps = redisTemplate.boundHashOps("user");
        String name1 = boundHashOps.get("name");
        System.out.println(name1);

    }
}
