package com.yxl.smmall.wares.controller;

import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.R;
import com.yxl.common.vo.MergeVO;
import com.yxl.smmall.wares.entity.WmsPurchaseEntity;
import com.yxl.smmall.wares.service.WmsPurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;



/**
 * 采购信息
 *
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-18 09:52:15
 */
@SuppressWarnings("ALL")
@RestController
@RequestMapping("wares/wmspurchase")
public class WmsPurchaseController {
    @Autowired
    private WmsPurchaseService wmsPurchaseService;

    /**
     * 模拟员工系统中的领取订单的功能，本功能不在后台管理系统中
     * @param ids 领取多个采购单，
     * @return
     */
    //TODO 员工只能领取分配给自己的采购单以及领取之前先查询没有被领取的采购单（细节功能完善）
    @PostMapping("/received")
    public R received(List<Long> ids){
        wmsPurchaseService.recevied(ids);
        return R.ok();
    }

    /**
     * 合并采购单，
     */
    @PostMapping("/merge")
    public R merge(@RequestBody MergeVO mergeVO) {
        wmsPurchaseService.mergePuchase(mergeVO);
    return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("wares:wmspurchase:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = wmsPurchaseService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * unreceive/list
     */

    @RequestMapping("/unreceive/list")
// @RequiresPermissions("wares:wmspurchase:list")
    public R unreceivelist(@RequestParam Map<String, Object> params) {
        PageUtils page = wmsPurchaseService.queryPageUnReceive(params);

        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("wares:wmspurchase:info")
    public R info(@PathVariable("id") Long id) {
        WmsPurchaseEntity wmsPurchase = wmsPurchaseService.getById(id);

        return R.ok().put("wmsPurchase", wmsPurchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
  //  @RequiresPermissions("wares:wmspurchase:save")
    public R save(@RequestBody WmsPurchaseEntity wmsPurchase){
        wmsPurchase.setUpdateTime(new Date());
        wmsPurchase.setUpdateTime(new Date());
		wmsPurchaseService.save(wmsPurchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("wares:wmspurchase:update")
    public R update(@RequestBody WmsPurchaseEntity wmsPurchase){
		wmsPurchaseService.updateById(wmsPurchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("wares:wmspurchase:delete")
    public R delete(@RequestBody Long[] ids){
		wmsPurchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
