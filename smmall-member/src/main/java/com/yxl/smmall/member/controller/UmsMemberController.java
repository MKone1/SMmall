package com.yxl.smmall.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.yxl.common.exception.BizCodeEnume;
import com.yxl.common.vo.SocialLoginVo;
import com.yxl.smmall.member.exception.PhoneExsitException;
import com.yxl.smmall.member.exception.UsernameExsitExcepation;
import com.yxl.smmall.member.feign.CouponFeginService;
import com.yxl.smmall.member.vo.MemberLoginVo;
import com.yxl.smmall.member.vo.MemberRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yxl.smmall.member.entity.UmsMemberEntity;
import com.yxl.smmall.member.service.UmsMemberService;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.R;



/**
 * 会员
 *
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-18 09:20:23
 */
@RestController
@RequestMapping("member/umsmember")
public class UmsMemberController {
    @Autowired
    private UmsMemberService umsMemberService;

    /**
     * Fegin测试
     *
     */
    @Autowired
    CouponFeginService couponFeginService;
    @RequestMapping("/coupons")
    public R test(){
        UmsMemberEntity memberEntity = new UmsMemberEntity();
        memberEntity.setNickname("张三");
        R membercoupons = couponFeginService.membercoupons();

        return R.ok().put("member",memberEntity).put("coupons",membercoupons.get("coupons"));
    }

    /**
     * 注册
     * @param memberRegisterVo
     * @return
     */
    @PostMapping("/register")
    public R register( @RequestBody MemberRegisterVo memberRegisterVo){
        try{
            umsMemberService.register(memberRegisterVo);
        }catch (PhoneExsitException e){
            R.error(BizCodeEnume.PHONE_EXSIT_EXCEPTION.getCode(),BizCodeEnume.PHONE_EXSIT_EXCEPTION.getMsg());
        }catch (UsernameExsitExcepation e){
            R.error(BizCodeEnume.USERNAME_EXSIT_EXCEPTION.getCode(),BizCodeEnume.USERNAME_EXSIT_EXCEPTION.getMsg());
        }
        return R.ok();
    }

    /**
     *
     * @param memberLoginVo
     * @return
     */
    @PostMapping("/login")
    public R login(@RequestBody  MemberLoginVo memberLoginVo){
       UmsMemberEntity memberEntity =  umsMemberService.login(memberLoginVo);
       if (memberEntity != null){
           return R.ok().setData(memberEntity);
       }else{
           return R.error(BizCodeEnume.USERNAMEORPASSWORD_EXCEPTION.getCode(),BizCodeEnume.USERNAMEORPASSWORD_EXCEPTION.getMsg());
       }

    }

    /**
     * 社交登录
     * @param socialLoginVo
     * @return
     */
    @PostMapping("/oauth/login")
    public R oauthLogin(@RequestBody SocialLoginVo socialLoginVo) throws Exception {
        //实现对如果是第一次登陆的用户的登陆以及注册
            UmsMemberEntity memberEntity =  umsMemberService.login(socialLoginVo);
            if (memberEntity != null){
                return R.ok().setData(memberEntity);
            }else{
                return R.error(BizCodeEnume.LOGIN_PASSWORD_INVAILD_EXCEPTION.getCode(),BizCodeEnume.LOGIN_PASSWORD_INVAILD_EXCEPTION.getMsg());
            }

    }
    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("member:umsmember:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = umsMemberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("member:umsmember:info")
    public R info(@PathVariable("id") Long id){
		UmsMemberEntity umsMember = umsMemberService.getById(id);

        return R.ok().put("umsMember", umsMember);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
  //  @RequiresPermissions("member:umsmember:save")
    public R save(@RequestBody UmsMemberEntity umsMember){
		umsMemberService.save(umsMember);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("member:umsmember:update")
    public R update(@RequestBody UmsMemberEntity umsMember){
		umsMemberService.updateById(umsMember);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("member:umsmember:delete")
    public R delete(@RequestBody Long[] ids){
		umsMemberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
