package com.yxl.smmall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxl.common.utils.HttpUtils;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.Query;
import com.yxl.common.vo.SocialLoginVo;
import com.yxl.smmall.member.dao.UmsMemberDao;
import com.yxl.smmall.member.dao.UmsMemberLevelDao;
import com.yxl.smmall.member.entity.UmsMemberEntity;
import com.yxl.smmall.member.entity.UmsMemberLevelEntity;
import com.yxl.smmall.member.exception.PhoneExsitException;
import com.yxl.smmall.member.exception.UsernameExsitExcepation;
import com.yxl.smmall.member.service.UmsMemberService;
import com.yxl.smmall.member.vo.MemberLoginVo;
import com.yxl.smmall.member.vo.MemberRegisterVo;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service("umsMemberService")
public class UmsMemberServiceImpl extends ServiceImpl<UmsMemberDao, UmsMemberEntity> implements UmsMemberService {
    @Autowired
    UmsMemberLevelDao memberLevelDao;
    @Autowired
    UmsMemberService memberService;
    @Autowired
    UmsMemberDao umsMemberDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<UmsMemberEntity> page = this.page(
                new Query<UmsMemberEntity>().getPage(params),
                new QueryWrapper<UmsMemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void register(MemberRegisterVo memberRegisterVo) {
        UmsMemberDao memberDao = this.baseMapper;
        UmsMemberEntity memberEntity = new UmsMemberEntity();
        //设置默认等级
        UmsMemberLevelEntity memberLevelEntity = memberLevelDao.getDefaultLevel();
        memberEntity.setLevelId(memberLevelEntity.getId());
        //设置联系方式和名称
        //检查用户以及电话是否是唯一
        /**
         * 如果电话用户名不唯一，如何返回给页面，页面展示相关的错误信息
         * 需要通过异常机制，通过感知特定的异常;
         * 通过自定义的异常，抛出这个异常UsernameExsitExcepation，PhoneExsitException
         * 异常通过调用方法抛给service层，在通过service抛给controller层，
         * 最后在controller层处理异常
         */
        checkPhoneUnique(memberRegisterVo.getPhone());
        memberEntity.setMobile(memberRegisterVo.getPhone());
        memberEntity.setUsername(memberRegisterVo.getUsername());
        /**
         * 对密码进行必要的加密处理
         * BCryptPasswordEncoder  (MD5盐值加密）
         * TODO：做笔记啊
         */
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(memberRegisterVo.getPassword());
        memberEntity.setPassword(encode);

        memberDao.insert(memberEntity);
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExsitException {
        UmsMemberDao baseMapper = this.baseMapper;
        Integer mobile = baseMapper.selectCount(new QueryWrapper<UmsMemberEntity>().eq("mobile", phone));
        if (mobile > 0) {
            throw new PhoneExsitException();
        }

    }

    @Override
    public void checkUsernameUnique(String username) throws UsernameExsitExcepation {
        UmsMemberDao baseMapper = this.baseMapper;
        Integer count = baseMapper.selectCount(new QueryWrapper<UmsMemberEntity>().eq("username", username));
        if (count > 0) {
            throw new UsernameExsitExcepation();
        }


    }

    @Override
    public UmsMemberEntity login(MemberLoginVo memberLoginVo) {
        String loginname = memberLoginVo.getLoginname();
        String password = memberLoginVo.getPassword();
        UmsMemberDao baseMapper = this.baseMapper;
        UmsMemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<UmsMemberEntity>().eq("username", loginname).or().eq("mobile", loginname));
        if (memberEntity != null) {
            //验证密码是否正确，将用户传递来的数据与数据库中加密后的数据
            String entityPassword = memberEntity.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            boolean matches = passwordEncoder.matches(password, entityPassword);
            if (matches) {
                return memberEntity;
            } else {
                return null;
            }

        } else {
            return null;
        }


    }

    /**
     * 登陆与注册和并在一起
     *
     * @param socialLoginVo
     * @return
     */
    @Override
    public UmsMemberEntity login(SocialLoginVo socialLoginVo) throws Exception {
        String uid = socialLoginVo.getUid();
        UmsMemberDao baseMapper = this.baseMapper;
        UmsMemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<UmsMemberEntity>().eq("social_uid", uid));
        if (memberEntity != null) {
            //说明已经在本系统中注册过了，需要对该用户进行更新操作
            UmsMemberEntity updated = new UmsMemberEntity();
            updated.setSocialUid(socialLoginVo.getUid());
            updated.setAccessToken(socialLoginVo.getAccess_token());
            updated.setExpiresIn(socialLoginVo.getExpires_in());
            //更新并返回的对象
            umsMemberDao.updateById(updated);
            memberEntity.setAccessToken(socialLoginVo.getAccess_token());
            memberEntity.setExpiresIn(socialLoginVo.getExpires_in());
            return memberEntity;
        } else {
            //说明在本系统中没有该社交用户，需要进行注册，通过发送请求获取到当前微博用户的用户信息

            UmsMemberEntity entity = new UmsMemberEntity();
            try {
                Map<String, String> headers = new HashMap<>();
                Map<String, String> map = new HashMap<>();
                map.put("uid", uid);
                map.put("access_token", socialLoginVo.getAccess_token());
//https://api.weibo.com/2/users/show.json?access_token=2.00gvrhLIcg2F3B93722e7963HCimxD&uid=7502057764
                HttpResponse httpResponse = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", headers, map);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    //表示查询成功

                    String json = EntityUtils.toString(httpResponse.getEntity());
                    JSONObject jsonObject = JSON.parseObject(json);
                    //昵称
                    String screen_name = jsonObject.getString("screen_name");
                    entity.setNickname(screen_name);
                    //名称
                    String name = jsonObject.getString("name");
                    entity.setUsername(name);
                    //邮箱
                    String email = jsonObject.getString("email");
                    entity.setEmail(email);
                    //性别
                    String gender = jsonObject.getString("gender");
                    entity.setGender("m".equals(gender) ? 1 : 0);
                    //城市
                    String city = jsonObject.getString("city");
                    entity.setCity(city);
                    //用户头像
                    String profile_image_url = jsonObject.getString("profile_image_url");
                    entity.setHeader(profile_image_url);
                }
            } catch (Exception e) {

            }
            //设置社交用户登陆的唯一Id
            entity.setSocialUid(uid);
            entity.setAccessToken(socialLoginVo.getAccess_token());
            entity.setExpiresIn(socialLoginVo.getExpires_in());
            umsMemberDao.insert(entity);
            return entity;

        }

    }

}