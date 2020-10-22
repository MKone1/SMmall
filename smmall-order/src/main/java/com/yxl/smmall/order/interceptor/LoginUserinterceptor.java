package com.yxl.smmall.order.interceptor;

import com.yxl.common.constant.AuthServiceConstant;
import com.yxl.common.vo.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginUserinterceptor implements HandlerInterceptor {
    //单线程数据共享
    public static ThreadLocal<MemberRespVo> loginUserResp = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        MemberRespVo attribute = (MemberRespVo) request.getSession().getAttribute(AuthServiceConstant.LOGIN_USER);
        if (attribute != null){
             //说明登陆了
            loginUserResp.set(attribute);
            return true;
        }else{
            //说明没有登陆
            request.getSession().setAttribute("msg","请先登录 ");
            response.sendRedirect("http://login.smmall.com/login.html");
            return false;
        }


    }
}
