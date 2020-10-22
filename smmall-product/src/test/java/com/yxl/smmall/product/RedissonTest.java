package com.yxl.smmall.product;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class RedissonTest {
    @AutoConfigureOrder
    RedissonClient redissonClient;
    @Test
    public void Redisson(){
        System.out.println(redissonClient);
    }
}
