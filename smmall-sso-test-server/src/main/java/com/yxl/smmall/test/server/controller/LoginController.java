package com.yxl.smmall.test.server.controller;

import com.sun.deploy.net.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.management.modelmbean.ModelMBeanOperationInfo;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author SADSADSD
 */
@Controller
public class LoginController {
    @Autowired
    StringRedisTemplate redisTemplate;
    @ResponseBody
    @GetMapping("/userinfo")
    public String userdata(@RequestParam("token") String token){
        String s = redisTemplate.opsForValue().get(token);
        return s;

    }


    @GetMapping("/login.html")
    public String loginPath(@RequestParam("redirect_url") String redirect_url,
                            Model model,
                            @CookieValue(value = "sso_token",required = false) String sso_token) {
        if (!StringUtils.isEmpty(sso_token)){
            //如果sso_token存在，则表示之前已经登陆，直接跳转
            return "redirect:"+redirect_url+"?"+"token="+sso_token;
        }
        model.addAttribute("url",redirect_url);
        return "login";
    }

    @PostMapping("/do")
    public String doLogin(@RequestParam ("name") String name ,
                          @RequestParam("password")  String password,
                          @RequestParam("url")  String url,
                          HttpServletResponse response) {
        if (!StringUtils.isEmpty(name)&&!StringUtils.isEmpty(password)){
            //登陆成功，跳回之前的页页面
            //将用户信息存入redis中
            String uuid = UUID.randomUUID().toString().replace("-", "");
            redisTemplate.opsForValue().set(uuid,name);
            //处理登录请求，登陆成功将用户信息存到Redis中，同时将令牌返回出去，并且给当前的服务系统留下一个记号（cookie）
            Cookie sso_token = new Cookie("sso_token", uuid);
            response.addCookie(sso_token);
            return "redirect:"+url+"?"+"token="+uuid;



        }
            //登陆失败，重新登陆
        return "login";

    }
}
