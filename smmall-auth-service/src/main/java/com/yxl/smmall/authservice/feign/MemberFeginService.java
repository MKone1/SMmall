package com.yxl.smmall.authservice.feign;

import com.yxl.common.utils.R;
import com.yxl.common.vo.SocialLoginVo;
import com.yxl.smmall.authservice.vo.LoginVo;
import com.yxl.smmall.authservice.vo.RegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author SADSADSD
 */
@FeignClient("smmall-member")
public interface MemberFeginService {
    /**
     * 注册
     * @param registerVo
     * @return
     */
    @PostMapping("member/umsmember/register")
    public R register(@RequestBody RegisterVo registerVo);

    /**
     * 登陆
     * @param loginVo
     * @return
     */
    @PostMapping("member/umsmember/login")
    public R login(@RequestBody LoginVo loginVo);

    /**
     * 社交登录
     * @param socialLoginVo
     * @return
     */
    @PostMapping("member/umsmember//oauth/login")
    public R oauthLogin(@RequestBody SocialLoginVo socialLoginVo);
}
