package com.yxl.smmall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.yxl.smmall.order.dao")
public class SmmallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmmallOrderApplication.class, args);
    }

}
