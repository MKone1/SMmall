package com.yxl.smmall.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.yxl.smmall.member.feign.CouponFeginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
