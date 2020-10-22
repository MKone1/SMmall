package com.yxl.smmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.vo.SocialLoginVo;
import com.yxl.smmall.member.entity.UmsMemberEntity;
import com.yxl.smmall.member.exception.PhoneExsitException;
import com.yxl.smmall.member.exception.UsernameExsitExcepation;
import com.yxl.smmall.member.vo.MemberLoginVo;
import com.yxl.smmall.member.vo.MemberRegisterVo;

import java.util.Map;

/**
 * 会员
 *
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-18 09:20:23
 */
public interface UmsMemberService extends IService<UmsMemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void register(MemberRegisterVo memberRegisterVo);

    void checkPhoneUnique(String phone) throws PhoneExsitException;

    void checkUsernameUnique(String username) throws UsernameExsitExcepation;

    UmsMemberEntity login(MemberLoginVo memberLoginVo);

    UmsMemberEntity login(SocialLoginVo socialLoginVo) throws Exception;
}

