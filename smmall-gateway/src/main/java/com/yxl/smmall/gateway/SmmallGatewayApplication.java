package com.yxl.smmall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 1，开启服务注册发现
 * (配置nacos注册中心地址）
 */
@EnableDiscoveryClient
@SpringBootApplication(exclude ={DataSourceAutoConfiguration.class} )
//排除数据库相关的jar包
public class SmmallGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmmallGatewayApplication.class, args);
    }

}
