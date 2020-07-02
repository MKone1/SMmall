package com.yxl.smmall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 1，想要远程调用别的服务
 *  1)，引用open-fegin
 *  2),编写一个接口，告诉Spring cloud这个接口需要调用的服务,就是服务的名称
 *  通过FeginClient注解绑定需要远程调用的服务，将服务的方法的完全类名放在接口里面
 *  （访问路径必须完整）
 *      a.声明接口的每一个方法都是调用的那个远程访问的那个请求
 *   3），开启远程服务功能@EnableFeginClients
 *          public @interface EnableFeignClients {
 *     String[] value() default {};
 *
 *     String[] basePackages() default {};
 *        基础包名
 *     Class<?>[] basePackageClasses() default {};
 *
 *     Class<?>[] defaultConfiguration() default {};
 *
 *     Class<?>[] clients() default {};
 * }
 *
 */
@EnableFeignClients(basePackages = "com.yxl.smmall.member.feign")
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.yxl.smmall.member.dao")
public class SmmallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmmallMemberApplication.class, args);
    }

}
