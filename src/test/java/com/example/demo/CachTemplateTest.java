package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class CachTemplateTest {

    Logger log = LoggerFactory.getLogger(CachTemplateTest.class);

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Before
    public void before() {
        redisTemplate.opsForValue().getOperations().delete("userIds");
    }

    /**
     * 防止缓存穿透
     */
    @Test
    public void testCach(){
        userMock();
    }

    /***
     * 多线程执行,模仿用户访问
     */
    public void userMock(){
        CountDownLatch cdl = new CountDownLatch(20);
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for(int i=0;i<21;i++){
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        log.info("result:"+getCost2());
                    }finally {
                        cdl.countDown();
                    }

                }
            };
            executor.execute(runnable);
        }
//        保证所有线程执行完
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executor.shutdown();

    }

    @Autowired
    private CachTemplate cachTemplate;

    public Cost getCost2(){
        return cachTemplate.getCach("userIds",60,new TypeReference<Cost>(){}, new CachCallBack() {
            @Override
            public <T> T exec() {
                Cost cost = new Cost();
                cost.setUscnt("1999");
                cost.setUsCost("999999.9999");
                cost.setDesc("用户总人数,与用户总金额");
                return (T) cost;
            }
        });
    }


    public Cost getCost1(){
        String key = "userIds";
        Cost cost = null;
        String cach =  redisTemplate.opsForValue().get(key)+"";

        if(StringUtils.isEmpty(cach)||"null".equals(cach)){
            synchronized (this){
                cach =  redisTemplate.opsForValue().get(key)+"";

                if(StringUtils.isEmpty(cach)||"null".equals(cach)){

                    cost = new Cost();
                    cost.setUscnt("1999");
                    cost.setUsCost("999999.9999");
                    cost.setDesc("用户总人数,与用户总金额");
                    String result = JSON.toJSONString(cost);
                    log.info("data ---------------------- "+result);
                    redisTemplate.opsForValue().set(key,result,60);
                    return cost;
                }else {

                    log.info("cach2 ---------------------- "+cach);
                    cost = JSON.parseObject(cach,Cost.class);
                    return cost;
                }

            }

        }else {
            cost = JSON.parseObject(cach,Cost.class);
            log.info("cach1 ---------------------- "+cost);
            return cost;

        }

    }
}


