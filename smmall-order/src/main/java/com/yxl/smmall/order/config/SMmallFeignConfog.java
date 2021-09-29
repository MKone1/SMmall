package com.yxl.smmall.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 创建一个Feign远程服务的拦截器
 */
@Configuration
public class SMmallFeignConfog {
    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                //利用Thread Local将当前线程的数据保存起来，拿到刚进来的请求
               ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
              if (attributes != null){
                  HttpServletRequest request = attributes.getRequest();//这里得到原本的请求
                  //同步请求头中的数据，Cookie
                  String cookie = request.getHeader("cookie");
                  //给新的请求添加上原本的请求中的Cookie
                  requestTemplate.header("Cookie",cookie);
              }

            }
        };
    }
}
