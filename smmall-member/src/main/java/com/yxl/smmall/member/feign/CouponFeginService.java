package com.yxl.smmall.member.feign;

import com.yxl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 这事一个声明式的远程调用
 */
@FeignClient("smmall-coupon")
public interface CouponFeginService {
    /**
     * 以后调用接口中的方法就回去到注册中心中先找远程服务smmall-coupon
     * 再去调用@RequestMapping("/coupon/smscoupon/member/list")请求对应的方法
     *
     * @return
     */
    @RequestMapping("/coupon/smscoupon/member/list")
    R membercoupons();
}
