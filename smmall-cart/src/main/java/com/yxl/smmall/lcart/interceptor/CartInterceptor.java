package com.yxl.smmall.lcart.interceptor;

import com.yxl.common.constant.AuthServiceConstant;
import com.yxl.common.constant.CartConstant;
import com.yxl.common.vo.MemberRespVo;
import com.yxl.smmall.lcart.to.UserInfoTo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/** 在执行目标方法之前判断是否登录
 * @author SADSADSD
 */

public class CartInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();
    /**
     * 目标方法执行之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfoTo userInfoTo = new UserInfoTo();
        HttpSession session = request.getSession();
        MemberRespVo memberRespVo= (MemberRespVo) session.getAttribute(AuthServiceConstant.LOGIN_USER);
        if (memberRespVo != null){
            userInfoTo.setUserid(memberRespVo.getId());
        }
        Cookie[] cookies = request.getCookies();
        if (cookies!=null&&cookies.length>0){
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if (name.equals(CartConstant.TEMP_USER_COOKIE_NAME)){
                    userInfoTo.setUserkey(cookie.getValue());
                    userInfoTo.setTempUser(true);
                }
            }
        }
        //如果没有临时用户一定要分配一个临时用户
        if (StringUtils.isEmpty(userInfoTo.getUserkey())){
            String s = UUID.randomUUID().toString();
            userInfoTo.setUserkey(s);
        }
        threadLocal.set(userInfoTo);
        return true;
    }

    /**
     * 业务执行之后将临时用户的信息发送给浏览器，浏览器保存一个临时用户的cookie，设置一个月过期
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {


        UserInfoTo userInfoTo = threadLocal.get();
        if (!userInfoTo.getTempUser()){
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME,userInfoTo.getUserkey());
            cookie.setDomain("smmall.com");
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);

            response.addCookie(cookie);
        }

    }
}
