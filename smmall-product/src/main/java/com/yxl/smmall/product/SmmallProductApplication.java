package com.yxl.smmall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 1，整合Mybatis-plus
 *      1），导入依赖
 *         <dependency>
 *             <groupId>com.baomidou</groupId>
 *             <artifactId>mybatis-plus-boot-starter</artifactId>
 *             <version>3.2.0</version>
 *         </dependency>
 *       2），配置数据库连接池
 *          a，导入数据库的驱动
 *          b，在application.yml配置数据相关信息
 *        3),配置Mybatis-plus
 *          a，
 *
 *
 * 2，利用mybatis实现逻辑删除
 *      a,配置全局的逻辑删除规则，这里我的配置文件放在了nacos配置中心里面
 *      B,配置组件bean（3.1.1之后可以不用配置）
 *      C，在对应的实体类的字段上添加一个注解@TableLogic
 *
 */
@EnableDiscoveryClient //开启注册服务发现功能
@MapperScan("com.yxl.smmall.product.dao")
@SpringBootApplication
public class SmmallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmmallProductApplication.class, args);
    }

}
