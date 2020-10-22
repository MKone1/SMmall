package com.yxl.smmall.lcart.fegin;

import com.yxl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author SADSADSD
 */
@FeignClient("smmall-product")
@Service
public interface AddToCartFegin {
    @RequestMapping("product/pmsskuinfo/info/{skuId}")
    //@RequiresPermissions("product:pmsskuinfo:info")
    public R info(@PathVariable("skuId") Long skuId);

    @GetMapping("product/pmsskusaleattrvalue/stringList/{skuid}")
    public List<String> getSkuList(@PathVariable("skuid") Long SkuId);

}
