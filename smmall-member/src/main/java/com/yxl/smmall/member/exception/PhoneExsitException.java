package com.yxl.smmall.member.exception;

/**
 * @author SADSADSD
 */
public class PhoneExsitException extends RuntimeException{

    public PhoneExsitException() {
        super("电话号码已存在");
    }
}
