package com.yxl.smmall.authservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author SADSADSD
 */
@Configuration
public class SMmallWebConfiguration  implements WebMvcConfigurer {
    /**
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry){
//        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/reg.html").setViewName("reg");

    }
}
