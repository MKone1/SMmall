package com.yxl.smmall.wares.feign;

import com.yxl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
@FeignClient("smmall-product")
@Service
public interface ProductFegin{
        @RequestMapping("/info/{skuId}")
        //@RequiresPermissions("product:pmsskuinfo:info")
        R info(@PathVariable("skuId") Long skuId);
}