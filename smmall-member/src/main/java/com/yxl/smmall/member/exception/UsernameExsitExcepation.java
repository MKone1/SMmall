package com.yxl.smmall.member.exception;

import org.omg.SendingContext.RunTime;

/**
 * @author SADSADSD
 */
public class UsernameExsitExcepation extends RuntimeException {
    public UsernameExsitExcepation() {
        super("用户名已存在");
    }
}
