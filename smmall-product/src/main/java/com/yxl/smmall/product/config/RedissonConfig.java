package com.yxl.smmall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**关于Redisson单节点模式的配置类
 * @author SADSADSD
 */
@Configuration
public class RedissonConfig {
    /**
     *报错：Caused by: java.lang.IllegalArgumentException: Redis url should start with redis:// or rediss:// (for SSL connection)
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod="shutdown")
    RedissonClient redisson() throws IOException {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.43.99:6379");
       //报错： OAUTH Authentication required.. channel: [id: 0xbf430b2d,
      //是因为设置列密码，添加密码即可
        config.useSingleServer().setPassword("123456");
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

}
