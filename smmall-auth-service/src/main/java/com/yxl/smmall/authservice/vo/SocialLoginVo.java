package com.yxl.smmall.authservice.vo;

import lombok.Data;

@Data
public class SocialLoginVo {
    private String access_token;
    private String remind_in;
    private long expires_in;
    private String uid;
    private String isRealName;
}
