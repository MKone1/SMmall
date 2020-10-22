package com.yxl.smmall.authservice.controller;

import com.alibaba.fastjson.TypeReference;
import com.yxl.common.constant.AuthServiceConstant;
import com.yxl.common.exception.BizCodeEnume;
import com.yxl.common.utils.R;
import com.yxl.common.vo.MemberRespVo;
import com.yxl.smmall.authservice.feign.MemberFeginService;
import com.yxl.smmall.authservice.feign.ThirdPartFeignService;
import com.yxl.smmall.authservice.vo.LoginVo;
import com.yxl.smmall.authservice.vo.RegisterVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.swing.text.html.parser.Entity;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author SADSADSD
 */
@Controller
public class LoginController {
    @Autowired
    ThirdPartFeignService thirdPartFeignService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    MemberFeginService memberFeginService;

    /**
     * 1,验证码防刷
     * <p>
     * 2，验证码再次校验,redis
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone) {
        String s = redisTemplate.opsForValue().get(AuthServiceConstant.SMS_CODE_CACHNE_PREFIX + phone);
        if (!StringUtils.isEmpty(s)) {
            long l = Long.parseLong(s.split("_")[1]);
            if (System.currentTimeMillis() - l < 60000) {
                return R.error(BizCodeEnume.SMS_CODE_EXCEPTION.getCode(), BizCodeEnume.SMS_CODE_EXCEPTION.getMsg());
            }
        }
        String code = UUID.randomUUID().toString().substring(0, 5);
        String substrings = code + "_" + System.currentTimeMillis();
        redisTemplate.opsForValue().set(AuthServiceConstant.SMS_CODE_CACHNE_PREFIX + phone, substrings, 10, TimeUnit.MINUTES);


        thirdPartFeignService.sendCode(phone, code);
        return R.ok();
    }

    /**
     * RedirectAttributes attr 模拟重定向携带数据
     *
     * @param registerVo
     * @param result
     * @param attr
     * @return
     */
    @PostMapping("/register")
    public String register(@Valid RegisterVo registerVo, BindingResult result, RedirectAttributes attr) {

        System.out.println(registerVo);
        if (result.hasErrors()) {
            //校验出错，转发到注册页
            Map<String, String> map = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (oldValue, newValue) -> newValue));
//            model.addAttribute("error",map);
            attr.addFlashAttribute("error", map);
            return "redirect:http://login.smmall.com/reg.html";
        }
        //开始注册，调用远程服务来实现对用户的检测
        //1，校验验证码
        String code = registerVo.getCode();
        String s = redisTemplate.opsForValue().get(AuthServiceConstant.SMS_CODE_CACHNE_PREFIX + registerVo.getPhone());
        if (!StringUtils.isEmpty(s)) {
            //验证是否正确，通过分割字符串验证，如果错误返回页面提示错误、
            if (!code.equals(s.split("_")[0])) {
                Map<String, String> map = new HashMap<>();
                map.put("code", "验证码错误，重试");
                attr.addFlashAttribute("error", map);
                return "redirect:http://login.smmall.com/reg.html";
            } else {
                // 验证成功之后，先删除Redis,在调用远程服务添加到数据库
                redisTemplate.delete(AuthServiceConstant.SMS_CODE_CACHNE_PREFIX + registerVo.getPhone());
                //调用远程服务
                R r = memberFeginService.register(registerVo);
                //判断是否执行成功
                if (r.getCode() == 0) {
                    //注册成功
                    return "redirect:http://login.smmall.com/login.htmll";
                } else {
                    //如果出现异常返回注册页面
                    Map<String, String> map = new HashMap<>();
                    map.put("msg", r.getData(new TypeReference<String>() {
                    }));
                    attr.addFlashAttribute("error", map);
                    return "redirect:http://login.smmall.com/reg.html";
                }
            }


        } else {
            //如果从缓存中没有找到验证码，这返回注册页面,并显示错误
            Map<String, String> map = new HashMap<>();
            map.put("code", "验证码过期，重试");
            attr.addFlashAttribute("error", map);
            return "redirect:http://login.smmall.com/reg.html";
        }
        //注册成功返回首页，返回登录页


    }
    @GetMapping("/login.html")
    public String login(HttpSession session){
        Object attribute = session.getAttribute(AuthServiceConstant.LOGIN_USER);
        if (attribute ==null){
            //没有登陆
            return "login";
        }else {
            return "redirect:http://smmall.com";
        }

    }


    @PostMapping("/login")
    public String loginPage(LoginVo loginVo, RedirectAttributes attr, HttpSession session){
        System.out.println(loginVo);
        //远程调用微服务
        R login = memberFeginService.login(loginVo);
        if(login.getCode() > 0){
            Map<String,String> errors  = new HashMap<>();
            String data = login.getData("msg", new TypeReference<String>() {
            });
            errors.put("msg",data);
            attr.addFlashAttribute("error",errors);
            return "redirect:http://login.smmall.com/login.html";
        }else {
            MemberRespVo data = login.getData("data", new TypeReference<MemberRespVo>() {});
            session.setAttribute(AuthServiceConstant.LOGIN_USER,data);
            return "redirect:http://smmall.com";
        }


    }
}
