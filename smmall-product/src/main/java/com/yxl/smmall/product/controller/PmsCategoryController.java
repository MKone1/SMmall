package com.yxl.smmall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yxl.smmall.product.entity.PmsCategoryEntity;
import com.yxl.smmall.product.service.PmsCategoryService;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.R;



/**
 * 商品三级分类
 *
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-25 17:12:26
 */
@RestController
@RequestMapping("product/pmscategory")
public class PmsCategoryController {
    @Autowired
    private PmsCategoryService pmsCategoryService;
    /**
     *
     */
    @RequestMapping("/list/tree")
    public  R ListTrees(){
 List<PmsCategoryEntity> entityList =   pmsCategoryService.listWith();
        return R.ok().put("data",entityList);
    }




    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("product:pmscategory:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = pmsCategoryService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    //@RequiresPermissions("product:pmscategory:info")
    public R info(@PathVariable("catId") Long catId){
		PmsCategoryEntity pmsCategory = pmsCategoryService.getById(catId);

        return R.ok().put("data", pmsCategory);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
  //  @RequiresPermissions("product:pmscategory:save")
    public R save(@RequestBody PmsCategoryEntity pmsCategory){
		pmsCategoryService.save(pmsCategory);

        return R.ok();
    }
    /**
     * 1，实现拖拽效果的后台的代码的编写，修改排序
     */
    @RequestMapping("/update/sort")
    // @RequiresPermissions("product:pmscategory:update")
    public R updateSort(@RequestBody PmsCategoryEntity[] pmsCategory){
        pmsCategoryService.updateBatchById(Arrays.asList(pmsCategory));
        return R.ok();
    }






    /**
     * 修改
     * 后台管理页面只需要修改，catId,name ,图标地址，计量单位
     */
    @RequestMapping("/update")
   // @RequiresPermissions("product:pmscategory:update")
    public R update(@RequestBody PmsCategoryEntity pmsCategory){
		pmsCategoryService.updateById(pmsCategory);

        return R.ok();
    }

    /**
     * 删除
     * @RequestBody 获取请求体，只有Post请求才有请求体，
     * Springmvc自动将请求体的数据（JSON)转换成相应的对象
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("product:pmscategory:delete")
    public R delete(@RequestBody Long[] catIds){
//		pmsCategoryService.removeByIds(Arrays.asList(catIds));

//        1，检查当前删除的菜单是否被其他的地方进行了引用
        pmsCategoryService.removeMebuById(Arrays.asList(catIds));
        return R.ok();
    }


}
