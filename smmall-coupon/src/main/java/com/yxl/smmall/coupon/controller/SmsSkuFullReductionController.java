package com.yxl.smmall.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import com.yxl.common.to.SkuReductionTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yxl.smmall.coupon.entity.SmsSkuFullReductionEntity;
import com.yxl.smmall.coupon.service.SmsSkuFullReductionService;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.R;



/**
 * 商品满减信息
 *
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-18 08:50:56
 */
@RestController
@RequestMapping("coupon/smsskufullreduction")
public class SmsSkuFullReductionController {
    @Autowired
    private SmsSkuFullReductionService smsSkuFullReductionService;

    /**
     * 列表
     */
    @PostMapping("/saveinfo")
   // @RequiresPermissions("coupon:smsskufullreduction:list")
    public R saceinfo (@RequestBody SkuReductionTO skuReductionTO){

smsSkuFullReductionService.saveSkuReduction(skuReductionTO);
        return R.ok();
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("coupon:smsskufullreduction:info")
    public R info(@PathVariable("id") Long id){
		SmsSkuFullReductionEntity smsSkuFullReduction = smsSkuFullReductionService.getById(id);

        return R.ok().put("smsSkuFullReduction", smsSkuFullReduction);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
  //  @RequiresPermissions("coupon:smsskufullreduction:save")
    public R save(@RequestBody SmsSkuFullReductionEntity smsSkuFullReduction){
		smsSkuFullReductionService.save(smsSkuFullReduction);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("coupon:smsskufullreduction:update")
    public R update(@RequestBody SmsSkuFullReductionEntity smsSkuFullReduction){
		smsSkuFullReductionService.updateById(smsSkuFullReduction);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("coupon:smsskufullreduction:delete")
    public R delete(@RequestBody Long[] ids){
		smsSkuFullReductionService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
