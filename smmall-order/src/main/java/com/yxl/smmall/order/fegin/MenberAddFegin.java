package com.yxl.smmall.order.fegin;

import com.yxl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author SADSADSD
 */
@Service
@FeignClient("smmall-member")
public interface MenberAddFegin {
    @RequestMapping("member/umsmemberreceiveaddress/user/{id}")
    //@RequiresPermissions("member:umsmemberreceiveaddress:info")
 R infoByUserId(@PathVariable("id") Long id);
}
