package com.yxl.common.exception;

import com.sun.javaws.jnl.IconDesc;

/***
 * 错误码和错误信息定义类
 * 1. 错误码定义规则为5为数字
 * 2. 前两位表示业务场景，最后三位表示错误码。例如：100001。10:通用 001:系统未知异常
 * 3. 维护错误码后需要维护错误描述，将他们定义为枚举形式
 * 错误码列表：
 *  10: 通用
 *      001：参数格式校验
 *      002: 短信验证码频率太高
 *  11: 商品
 *  12: 订单
 *  13: 购物车
 *  14: 物流
 *  15,注册登陆
 *  16,社交登录
 *21
 *
 */
public enum  BizCodeEnume {

    UNKNOW_EXCEPTION(10000,"系统未知异常"),
    VAILD_EXCEPTION(10001,"参数格式校验失败"),
    SMS_CODE_EXCEPTION(10001,"短信验证码频率太高，稍后再试"),
    PRODUCT_UP_EXCEPTION(1111,"商品上架异常"),
    PHONE_EXSIT_EXCEPTION(1501,"电话号码已被注册"),
    USERNAME_EXSIT_EXCEPTION(1502,"用户名已被注册"),
    USERNAMEORPASSWORD_EXCEPTION(1503,"用户名或密码错误"),
    LOGIN_PASSWORD_INVAILD_EXCEPTION(1601,"社交登录用户名或密码错误"),
    ITEM_OF_WARES_EXCEPTION(2101,"该商品没有库存，及时添加")
    ;

    private int code;
    private String msg;
    BizCodeEnume(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
