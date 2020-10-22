package com.yxl.smmall.lcart.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author SADSADSD
 */
//@EnableConfigurationProperties(ThreadPoolConfigProperties.class)
@Configuration
public class MyThreadConfig {
    @Bean
    public  ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties poolConfigProperties){
        return  new ThreadPoolExecutor(poolConfigProperties.getCoreSize(),poolConfigProperties.getMaxSizes(),
                poolConfigProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,new LinkedBlockingDeque<>(100000),
                Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
    }
}
