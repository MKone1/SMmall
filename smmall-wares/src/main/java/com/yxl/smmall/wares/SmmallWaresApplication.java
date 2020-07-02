package com.yxl.smmall.wares;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.yxl.smmall.wares.dao")
public class SmmallWaresApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmmallWaresApplication.class, args);
    }

}
