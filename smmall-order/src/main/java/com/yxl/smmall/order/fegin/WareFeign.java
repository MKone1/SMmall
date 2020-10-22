package com.yxl.smmall.order.fegin;

import com.yxl.common.utils.R;
import com.yxl.common.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
@FeignClient("smmall-wares")
public interface WareFeign {

    @PostMapping("/wares/wmswaresku/hasstock")
    R getSkusHasStock(@RequestBody List<Long> skuIdList);

    @GetMapping("wares/wmswareinfo/Fare")
    R getFare(@RequestParam("addrId") Long addrId);


    @PostMapping("/wares/wmswaresku/lock/order")
    R orderLockStock(@RequestBody WareSkuLockVo vo);
}
