package com.yxl.smmall.wares.feign;

import com.yxl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@FeignClient("smmall-member")
public interface MemberAddrFeign {
    @RequestMapping("/member/umsmemberreceiveaddress/info/{id}")
     R Addrinfo(@PathVariable("id") Long id);
}
