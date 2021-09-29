package com.yxl.smmall.product.app;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yxl.smmall.product.entity.PmsAttrAttrgroupRelationEntity;
import com.yxl.smmall.product.service.PmsAttrAttrgroupRelationService;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.R;



/**
 * 属性&属性分组关联
 *
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-25 17:12:26
 */

@RestController
@RequestMapping("product/pmsattrattrgrouprelation")
public class PmsAttrAttrgroupRelationController {
    @Autowired
    private PmsAttrAttrgroupRelationService pmsAttrAttrgroupRelationService;

    /**
     * 列表
     */
    @RequestMapping("/list")
   // @RequiresPermissions("product:pmsattrattrgrouprelation:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = pmsAttrAttrgroupRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:pmsattrattrgrouprelation:info")
    public R info(@PathVariable("id") Long id){
		PmsAttrAttrgroupRelationEntity pmsAttrAttrgroupRelation = pmsAttrAttrgroupRelationService.getById(id);

        return R.ok().put("pmsAttrAttrgroupRelation", pmsAttrAttrgroupRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
  //  @RequiresPermissions("product:pmsattrattrgrouprelation:save")
    public R save(@RequestBody PmsAttrAttrgroupRelationEntity pmsAttrAttrgroupRelation){
		pmsAttrAttrgroupRelationService.save(pmsAttrAttrgroupRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("product:pmsattrattrgrouprelation:update")
    public R update(@RequestBody PmsAttrAttrgroupRelationEntity pmsAttrAttrgroupRelation){
		pmsAttrAttrgroupRelationService.updateById(pmsAttrAttrgroupRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("product:pmsattrattrgrouprelation:delete")
    public R delete(@RequestBody Long[] ids){
		pmsAttrAttrgroupRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
