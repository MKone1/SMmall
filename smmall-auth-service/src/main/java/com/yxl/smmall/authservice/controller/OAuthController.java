package com.yxl.smmall.authservice.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yxl.common.utils.HttpUtils;
import com.yxl.common.utils.R;
import com.yxl.common.vo.SocialLoginVo;
import com.yxl.smmall.authservice.feign.MemberFeginService;

import com.yxl.common.vo.MemberRespVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class OAuthController {
    @Autowired
    MemberFeginService memberFeginService;
    @GetMapping("/oauth2.0/weibo/success")
    public String weiboLogin(@RequestParam("code") String code , HttpSession session) throws Exception {
        System.out.println(code);

        Map<String,String> map = new HashMap<>();

        map.put("client_id","1242487230");
        map.put("client_secret","ecd1df661adbde51f95108a3bbbd9454");
        map.put("grant_type","authorization_code");
        map.put("redirect_uri","http://login.smmall.com/oauth2.0/weibo/success");
        map.put("code",code);
//https://api.weibo.com/oauth2/access_token?client_id=1242487230&client_secret=ecd1df661adbde51f95108a3bbbd9454&grant_type=authorization_code&redirect_uri=http://login.smmall.com/oauth2.0/weibo/success&code=7f7c3618e8fb23dc97f650f93aa84887
        //1.0通过code获取到accesstoken 令牌，通过阿里的HttpUtil发送请求
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", new HashMap<>(), map, new HashMap<>());
        //处理回调
        if (response.getStatusLine().getStatusCode() == 200){
            String s = EntityUtils.toString(response.getEntity());
            SocialLoginVo socialLoginVo = JSON.parseObject(s, SocialLoginVo.class);

            //需要判断数据库中是否有着一个用户，如果没有则将该用户注册
            //调用远程服务实现登陆注册（会员服务）smmall-member
            R r = memberFeginService.oauthLogin(socialLoginVo);
            if (r.getCode() > 0 ){
                //远程调用失败
                return "redirect:http://login.smmall.com/login.html";
            }else{
                //远程调用成功
                MemberRespVo data = r.getData("data", new TypeReference<MemberRespVo>() {
                });
                log.info("登陆成功"+data.toString());
                /**
                 * 通过redis+springSession实现单点登录
                 * TODO：1，默认发的令牌。session的作用域是当前域（解决子域session共享的问题）
                 * TODO：2，希望在Redis存储的数据以JSON的格式存储
                 */
                session.setAttribute("loginUser",data);
                return "redirect:http://smmall.com";
            }


        }else {
            return "redirect:http://login.smmall.com/login.html";
        }


    }
}
