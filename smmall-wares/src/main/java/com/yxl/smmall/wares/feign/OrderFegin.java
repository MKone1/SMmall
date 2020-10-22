package com.yxl.smmall.wares.feign;

import com.yxl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@FeignClient("smmall-order")
public interface OrderFegin {
    @GetMapping("order/omsorder/status/{orerSn}")
    public R getOrderStatus(@PathVariable("orderSn") String s);
}
