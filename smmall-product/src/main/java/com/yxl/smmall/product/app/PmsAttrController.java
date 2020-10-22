package com.yxl.smmall.product.app;

import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.R;
import com.yxl.smmall.product.service.PmsAttrService;
import com.yxl.smmall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 商品属性
 *
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-25 17:12:26
 */
@RestController
@RequestMapping("product/pmsattr")
public class PmsAttrController {
    @Autowired
    private PmsAttrService pmsAttrService;


    ///product/pmsattr/base/list/{catelogId}\
    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseAttrList(@RequestParam Map<String, Object> params,
                          @PathVariable("catelogId") Long catelogId,
                          @PathVariable("attrType") String Type) {
//        System.out.println(catelogId);
//        PageUtils page = pmsAttrService.queryPage(params);
        PageUtils page = pmsAttrService.queryBaseAttrPage(params, catelogId,Type);
        return R.ok().put("page", page);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:pmsattr:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = pmsAttrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:pmsattr:info")
    public R info(@PathVariable("attrId") Long attrId){
//		PmsAttrEntity pmsAttr = pmsAttrService.getById(attrId);
AttrVo attrVo= pmsAttrService.getAttrInfo(attrId);
        return R.ok().put("pmsAttr", attrVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //  @RequiresPermissions("product:pmsattr:save")
    public R save(@RequestBody AttrVo pmsAttr) {
        pmsAttrService.saveAttr(pmsAttr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("product:pmsattr:update")
    public R update(@RequestBody AttrVo pmsAttr){
		pmsAttrService.updatAttr(pmsAttr);

        return R.ok();
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("product:pmsattr:delete")
    public R delete(@RequestBody Long[] attrIds){
		pmsAttrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
