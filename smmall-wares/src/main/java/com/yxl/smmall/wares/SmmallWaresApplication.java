package com.yxl.smmall.wares;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
@EnableRabbit
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.yxl.smmall.wares.dao")
@EnableFeignClients
public class SmmallWaresApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmmallWaresApplication.class, args);
    }

}
