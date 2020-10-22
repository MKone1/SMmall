package com.yxl.smmall.authservice.vo;

import lombok.Data;
import org.checkerframework.checker.units.qual.Length;


import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/** 注册数据封装
 * @author SADSADSD
 */
@Data
public class RegisterVo {

    @NotEmpty(message = "用户名必须提交")
    @Size( min =6 ,max = 18 ,message = "用户名最小6位，最大18位")
    private String username;
    @NotEmpty(message = "密码必须提交")
    @Size( min =6 ,max = 18 ,message = "密码最小6位，最大18位")
    private String password;
    @NotEmpty(message = "验证码不能为空")
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$",message = "手机号格式不正确")
    private String phone;
    @NotEmpty(message = "验证码不能为空")
    private String code;

}
