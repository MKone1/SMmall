package com.yxl.smmall.order.fegin;

import com.yxl.common.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
@FeignClient("smmall-cart")
public interface OrderProducesFegin {
    @GetMapping("/getItems")
    public List<OrderItemVo> CartItems();
}
