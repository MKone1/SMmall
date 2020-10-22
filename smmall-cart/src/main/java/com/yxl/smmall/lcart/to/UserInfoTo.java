package com.yxl.smmall.lcart.to;

import lombok.Data;

@Data
public class UserInfoTo {
    private Long userid;
    private String userkey;
    private Boolean tempUser = false;
}
