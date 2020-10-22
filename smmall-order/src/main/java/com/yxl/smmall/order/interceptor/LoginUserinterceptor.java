package com.yxl.smmall.order.interceptor;

import com.yxl.common.constant.AuthServiceConstant;
import com.yxl.common.vo.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginUserinterceptor implements HandlerInterceptor {
    //单线程数据共享
    public static ThreadLocal<MemberRespVo> loginUserResp = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//    拦截器拦截了服务之间的调度的，将这个路径拦截，没有登陆信息就会跳转到登陆界面，这里通过对这个路径进行放行
//     order/omsorder/status/{orerSn}
        String requestURI = request.getRequestURI();//获取路径
//        AntPathMatch路径匹配器
        boolean match = new AntPathMatcher().match("order/omsorder/status/**", requestURI);
        if (match){
            return  true;
        }
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
