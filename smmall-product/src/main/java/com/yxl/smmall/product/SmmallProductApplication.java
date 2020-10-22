package com.yxl.smmall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

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
 * 3，实现后端的数据校验：JSR303
 *     a,给bean添加校验注解
 *     b，开启校验功能@Volid，
 *     c,校验的bean后紧跟一个BindingResult，就可以获取到检验的结果
 *     d,分组校验功能：（多场景的复杂校验）
 *          1，给校验注解标注什么情况需要进行数据校验
 *          2,@Validated(AddGroup.class)
 *          3,默认没有在指定分组的校验的注解的，在分组校验的情况下不会生效，只会在不分组的情况下生效
 *     e.自定义校验规则：
 *          1，编写一个自定义的校验注解
 *          public @interface ListValue {
 *          2，编写一个自定义的校验器
 *          public class ListValueValidated implements ConstraintValidator<ListValue,Integer>
 *          3，关联自定义的校验注解与校验器：
 *          这里我们可以指定多个校验器,实现多个功能的校验
 *          @Constraint(
 *         validatedBy = {ListValueValidated.class}
 *          )
 *
 * 4,统一异常处理
 * @ControllerAdvice
 * 1),白那些异常处理类，使用@ControllerAdvice
 * 2),使用@ExceptionHadler标注方法可以处理的异常
 *
 * 5，模板引擎 thymeleaf
 * a,导入starter
 * b,关闭缓存
 * c,静态资源都放在static文件夹下，可以根据路径直接访问
 * d,页面放在template下面
 * E,页面修改不重启服务实现更新
 *      导入   <dependency>
 *             <groupId>org.springframework.boot</groupId>
 *             <artifactId>spring-boot-devtools</artifactId>
 *             <optional>true</optional>
 *         </dependency>
 *       使用时修改完页面CTRL shift F9重新自动编译页面，
 *       如果修改了代码以及配置文件时还是重启服务
 *6，整合Redis
 * a,引入data-redis-starter
 * b,简单配置redis的host信息
 * c,实现对部分数据存储
 * d,实现分布式锁
 * 7，整合Redisson作为分布式锁等功能
 *a,导入依赖
 * b,进行配置
 */
@EnableRedisHttpSession
@EnableFeignClients(basePackages = "com.yxl.smmall.product.fegin")
@EnableDiscoveryClient //开启注册服务发现功能
@MapperScan("com.yxl.smmall.product.dao")
@SpringBootApplication
public class  SmmallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmmallProductApplication.class, args);
    }

}
