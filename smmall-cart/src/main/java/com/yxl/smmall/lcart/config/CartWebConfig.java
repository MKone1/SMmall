package com.yxl.smmall.lcart.config;

import com.yxl.smmall.lcart.interceptor.CartInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author SADSADSD
 */
@Configuration
public class CartWebConfig  implements WebMvcConfigurer {
    /**
     * 拦截购物车的所有请求
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CartInterceptor()).addPathPatterns("/**");
    }
}
