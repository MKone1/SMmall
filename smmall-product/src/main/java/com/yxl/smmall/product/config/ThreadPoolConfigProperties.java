package com.yxl.smmall.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author SADSADSD
 */

@Component
@Data
public class ThreadPoolConfigProperties {
    private Integer coreSize = 20;
    private Integer maxSizes = 200;
    private  Integer keepAliveTime = 10;

}
