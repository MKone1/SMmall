package com.yxl.smmall.product.config;


import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
@EnableConfigurationProperties(CacheProperties.class)
@Configuration
@EnableCaching
public class MyCacheConfig {
    // 第一种：由于CacheProperties已经在容器中了，只需要将它拿过来用
    @Autowired
    CacheProperties cacheProperties;
//    TODO:这里的配置文件无效, 已解决，问题是，原先添加了关于Redis的配置类，删除之后就可以了，在Redis的配置类里面配置了关于K，V序列化的问题

    /**
     * 配置类，配置文件没有生效：查看官方自动配置类
     * 第二种：redisCacheConfiguration是向容器中添加Bean，传递的参数会自动从容器中获取CacheProperties,CacheProperties cacheProperties
     * @return
     */
    @Bean
     RedisCacheConfiguration RedisCacheConfiguration(){
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        config  = config.serializeKeysWith(RedisSerializationContext.SerializationPair.
                fromSerializer(new StringRedisSerializer()));
        config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.
                fromSerializer(new GenericFastJsonRedisSerializer()));
// 将配置文件中的所有配置全部配置上，
        //但是，redisProperties从哪里来？
        /**1，与配置文件绑定的配置类
         * @ConfigurationProperties(
         *     prefix = "spring.cache"
         * )
         * public class CacheProperties
         * 这个类并没有放在容器中，需要添加一个注解将这个类放在容器中
         */
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
    return config;
    }

}
