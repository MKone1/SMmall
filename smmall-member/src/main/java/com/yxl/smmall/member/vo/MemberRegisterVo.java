package com.yxl.smmall.member.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author SADSADSD
 */
@Data
public class MemberRegisterVo {
    private String username;
    private String password;
    private String phone;

}