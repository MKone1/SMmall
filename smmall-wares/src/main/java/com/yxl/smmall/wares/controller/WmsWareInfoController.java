package com.yxl.smmall.wares.controller;

import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.R;
import com.yxl.common.vo.FareVO;
import com.yxl.smmall.wares.entity.WmsWareInfoEntity;
import com.yxl.smmall.wares.service.WmsWareInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;



/**
 * 仓库信息
 *
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-18 09:52:15
 */
@RestController
@RequestMapping("wares/wmswareinfo")
public class WmsWareInfoController {
    @Autowired
    private WmsWareInfoService wmsWareInfoService;

    /**
     * 查询运费多少
     * @param addrId
     * @return
     */
    @GetMapping("/Fare")
    public R getFare(@RequestParam("addrId") Long addrId) {
        FareVO fares = wmsWareInfoService.getFare(addrId);
        System.out.println(fares.toString());
      return R.ok().put("data",fares);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("wares:wmswareinfo:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = wmsWareInfoService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("wares:wmswareinfo:info")
    public R info(@PathVariable("id") Long id){
		WmsWareInfoEntity wmsWareInfo = wmsWareInfoService.getById(id);

        return R.ok().put("wmsWareInfo", wmsWareInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
  //  @RequiresPermissions("wares:wmswareinfo:save")
    public R save(@RequestBody WmsWareInfoEntity wmsWareInfo){
		wmsWareInfoService.save(wmsWareInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("wares:wmswareinfo:update")
    public R update(@RequestBody WmsWareInfoEntity wmsWareInfo){
		wmsWareInfoService.updateById(wmsWareInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("wares:wmswareinfo:delete")
    public R delete(@RequestBody Long[] ids){
		wmsWareInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
