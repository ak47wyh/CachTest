package com.example.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Autowired
    RedisTemplate<String,String> redisTemplate;
    @Test
    public void test(){
        redisTemplate.opsForValue().set("test","111111111");
        System.out.println(redisTemplate.opsForValue().get("test"));
    }
}
