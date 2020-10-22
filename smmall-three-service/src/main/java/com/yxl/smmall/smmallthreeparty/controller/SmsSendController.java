package com.yxl.smmall.smmallthreeparty.controller;

import com.yxl.common.utils.R;
import com.yxl.smmall.smmallthreeparty.component.SmmallComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**这个controller并不是提供给前台页面进行数据传递，而是在各个微服务之间调用
 * @author SADSADSD
 */
@RestController
@RequestMapping("/sms")
public class SmsSendController {
    @Autowired
    SmmallComponent smmallComponent;

    /**
     *
     * @param phone
     * @param code
     * @return
     */
    @GetMapping("/sendCode")
    public R sendCode(@RequestParam("phone") String phone,@RequestParam("code") String code){
        smmallComponent.sendSmmallCode(phone,code);
        return R.ok();
    }
}
