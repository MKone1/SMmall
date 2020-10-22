package com.yxl.smmall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * /**
 *  * 1,导入依赖
 *  * 2，编写配置,给容器中注入一个RestHighLevelClient
 *  3,参照官方文档
 *  */
@EnableRedisHttpSession
@EnableDiscoveryClient
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class  SmmallSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmmallSearchApplication.class, args);
    }

}
