package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CachTemplate {
    Logger log = LoggerFactory.getLogger(CachTemplate.class);

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    public <T> T getCach(String key, int expire, TypeReference<T> clazz, CachCallBack callBack){
        String cach =  redisTemplate.opsForValue().get(key)+"";

        if(!StringUtils.isEmpty(cach)&&!"null".equals(cach)){
            log.info("cach1 ---------------------- ");
            return JSON.parseObject(cach,clazz);

        }else {
            synchronized (this){
                cach =  redisTemplate.opsForValue().get(key)+"";

                if(!StringUtils.isEmpty(cach)&&!"null".equals(cach)){
                    log.info("cach2 ---------------------- ");
                    return JSON.parseObject(cach,clazz);

                }
                T result = callBack.exec();
                log.info("dataBase ---------------------- ");
                redisTemplate.opsForValue().set(key, JSON.toJSONString(result),60);
                return result;


            }


        }

    }
}
