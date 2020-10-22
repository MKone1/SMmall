package com.yxl.smmall.test.client.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HelloController {


    @Value("${ssoserver.url}")
    String loginUrl ;
    /**
     * 无需登陆
     * @return
     */
    @ResponseBody
    @GetMapping("/hello")
    public String hello(){

        return "hello";
    }

    /**
     * 需要进行登陆
     * @return
     */
    @GetMapping("/boss")
    public String emloyees(Model model, HttpSession session,@RequestParam(value="token",required = false) String token){
       if (!StringUtils.isEmpty(token)){
           //如果登陆成功就会传递回来一个token令牌
           //TODO:假设在客户端不知道redis的路径，所以只能去SSO系统获取用户数据
           RestTemplate restTemplate = new RestTemplate();
           ResponseEntity<String> forEntity = restTemplate.getForEntity("http://ssoserver.com:8080/userinfo?token=" + token, String.class);
           String body = forEntity.getBody();
           session.setAttribute("loginUser",body);
       }
        Object loginUser = session.getAttribute("loginUser");
        if (loginUser != null){
            //表示已经登陆，session已经存在
            List<String> list = new ArrayList<>();
            list.add("张三");
            list.add("25");
            list.add("成都");
            model.addAttribute("emloyees",list);
            return "boss";
        }else{
            //表示没有登陆，需要前往server进行登陆,添加一个URL，作为参数传递给服务端，
            //提示登陆后应该返回那个系统界面 ?redirect_url=http://client1.com:8081/emloyees
            return "redirect:"+loginUrl+"?"+"redirect_url=http://client2.com:8082/boss";
        }

    }
}
