package com.yxl.smmall.coupon;
/**
 * 如何使用nacos作为配置中心统一配置
 * 1，引入依赖
 *    <dependency>
 *             <groupId>com.alibaba.cloud</groupId>
 *             <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
 *         </dependency>
 *  2，创建一个b。bootstrap,properties
 *  配置一个当前应用的名字和nacos的地址
 *  spring.application.name=smmall-coupon
 * spring.cloud.nacos.config.server-addr=127.0.0.1:8848
 *  3，需要个配置中心添加一个叫数据集DataID smmall-coupon默认规则是 应用名.properties
 *  4，给 应用名.properties添加任何配置
 *  5,动态获取配置
 *      @RefreshScope：动态获取并刷新配置
 *      添加在controller中的
 * @Valiue("${配置项的名}”)
 *
 *      如果配置文件中有个和配置中心相同的配置文件，默认优先使用配置中心的
 *细节：
 *  1.命名空间：配置隔离（默认是public保留空间，默认新增的所有配置都在public中）
 *      a,开发，测试，生产等等诸多配置 (想要使用哪个命名空间就
 *          在bootstrap.properties中添加一个spring.cloud.nacos.config.namespace=命名空间ID
 *          利用命名空间来做环境隔离
 *      b,每一个微服务之间相互隔离，每个微服务都创建自己的命名空间，只加载自己命名空间下的所有配置
 *  2，配置集:(一组相关或者不相关的配置项的集合称为配置集，在系统中，一个配置文件通常就是一个配置集，包含了系统各个方面。
 *      例如，一个配置集可能包含了数据源，线程池，日志级别等配置项） 所有配置的集合
 *  3，配置集Id ：类似文件名  DataID
 *  4，配置分组： 默认的数据集都属于DEFAULT_GROUP；例如：双十一：1111    双十二：1212 等等
 *
 * 每个微服务创建自己的命名空间，使用配置分组区分环境，test，dev,prop
 *  5,同时加载多个配置集
 *      a,微服务任何配置信息，任何人配置文件都可以放在配置中心中
 *      b,只需要在bootstrap.properties说明加载配置中心的那些文件就可以了
 *      eg:
 *          spring.cloud.nacos.config.extension-configs[1].data-id=mybatis.yml
 *          spring.cloud.nacos.config.extension-configs[1].group=dev
 * s        spring.cloud.nacos.config.extension-configs[1].refresh=true
 *       c,@Value,@Configurationproperties......
 *       以前spring boot任何方法从配置文件中获取取值，都能使用，配置中心有的优先加载配置中心的
 *       项目可以通过boost rap配置文件指定配置中心的配置文件然后加载，不需要在项目中有过度的配置文件
 */

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.yxl.smmall.coupon.dao")
public class SmmallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmmallCouponApplication.class, args);
    }


}
