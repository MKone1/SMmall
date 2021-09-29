package com.yxl.smmall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 使用rabbitmq:
 * 1,引入amqpqi启动器，
 */
@EnableRedisHttpSession
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@EnableRabbit
@MapperScan("com.yxl.smmall.order.dao")
public class SmmallOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmmallOrderApplication.class, args);
    }

}
