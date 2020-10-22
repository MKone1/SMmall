package com.yxl.smmall.order.fegin;

import com.yxl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@FeignClient("smmall-coupon")
public interface CouponFegin {
    @GetMapping("coupon/smscoupon/getCoupon")
    R getCoupon();
}
