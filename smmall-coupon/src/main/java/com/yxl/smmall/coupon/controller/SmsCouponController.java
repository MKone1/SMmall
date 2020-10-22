package com.yxl.smmall.coupon.controller;

import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.R;
import com.yxl.smmall.coupon.entity.SmsCouponEntity;
import com.yxl.smmall.coupon.service.SmsCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 优惠券信息
 *
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-18 08:50:56
 */
@RefreshScope //刷新配置
@RestController
@RequestMapping("coupon/smscoupon")
public class SmsCouponController {
    @Autowired
    private SmsCouponService smsCouponService;


    /**
     * 测试nacos配置中心
      */
    @Value("${coupon.user.name}")
    private String name;
    @Value("${coupon.user.age}")
    private int age;
    @RequestMapping("test")
    public R test(){
return R.ok().put("name",name).put("age",age);
    }



    /**
     * 测试nacos注册服务
     *
     */
    @RequestMapping("/member/list")
    public R membercoupons() {
        SmsCouponEntity smsCouponEntity = new SmsCouponEntity();
        smsCouponEntity.setCouponName("满100减8");
        return R.ok().put("coupons", Arrays.asList(smsCouponEntity));
    }
    @GetMapping("/getCoupon")
    public R getCoupon(){
      List<SmsCouponEntity> list = smsCouponService.getCoupon();
      return R.ok().put("getCouponList",list);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("coupon:smscoupon:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = smsCouponService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("coupon:smscoupon:info")
    public R info(@PathVariable("id") Long id){
		SmsCouponEntity smsCoupon = smsCouponService.getById(id);

        return R.ok().put("smsCoupon", smsCoupon);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
  //  @RequiresPermissions("coupon:smscoupon:save")
    public R save(@RequestBody SmsCouponEntity smsCoupon){
		smsCouponService.save(smsCoupon);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("coupon:smscoupon:update")
    public R update(@RequestBody SmsCouponEntity smsCoupon){
		smsCouponService.updateById(smsCoupon);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("coupon:smscoupon:delete")
    public R delete(@RequestBody Long[] ids){
		smsCouponService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
