package com.yxl.smmall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;

import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class SMMallCorsConfiguration {
    /**
     * springboot自动配置了一个
     *
     public class CorsWebFilter implements WebFilter {
     private final CorsConfigurationSource configSource;
     private final CorsProcessor processor;

     */
    @Bean
    public CorsWebFilter corsWebFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //new一个跨域配置类，并对该对象设置
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        /**
         *     @Nullable
         *     private List<String> allowedOrigins;
         *     @Nullable
         *     private List<String> allowedMethods;
         *     @Nullable
         *     private List<HttpMethod> resolvedMethods;
         *     @Nullable
         *     private List<String> allowedHeaders;
         *     @Nullable
         *     private List<String> exposedHeaders;
         *     @Nullable
         *     private Boolean allowCredentials;
         *     @Nullable
         *     private Long maxAge;
         */
        corsConfiguration.addAllowedHeader("*");//允许那些数据头
        corsConfiguration.addAllowedMethod("*");//允许那些请求方法
        corsConfiguration.addAllowedOrigin("*");//允许那个请求来源
        corsConfiguration.setAllowCredentials(true);//是否允许携带cookie，true，否则跨域请求就会丢失



        /**注册跨域的配置,传递一个路径和一个CorsConfiguration
         *  public void registerCorsConfiguration(String path, CorsConfiguration config) {
         *         this.corsConfigurations.put(this.patternParser.parse(path), config);
         *     }
         */
        source.registerCorsConfiguration("/**",corsConfiguration);//   "/**",即表示任意路径






        /**
         *CorsConfigurationSource是一个接口，它的实现类中有一个UrlBasedCorsConfigurationSource的响应式编程
        CorsConfigurationSource configSource = new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(javax.servlet.http.HttpServletRequest httpServletRequest) {
                return null;
            }
        }
         */
        return new CorsWebFilter(source);
    }
}
