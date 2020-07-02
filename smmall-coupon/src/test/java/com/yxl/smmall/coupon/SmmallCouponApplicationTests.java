package com.yxl.smmall.coupon;

import com.yxl.smmall.coupon.entity.SmsCouponEntity;
import com.yxl.smmall.coupon.service.SmsCouponService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SmmallCouponApplicationTests {
    @Autowired
    SmsCouponService couponService;
    @Test
    void contextLoads() {
        SmsCouponEntity couponEntity = new SmsCouponEntity();
        couponEntity.setCouponName("618满减");
        couponService.save(couponEntity);
        System.out.println("保存成功");

    }

}
