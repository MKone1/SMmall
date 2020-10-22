package com.yxl.smmall.product.app;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.R;
import com.yxl.smmall.product.entity.PmsBrandEntity;
import com.yxl.smmall.product.entity.PmsCategoryBrandRelationEntity;
import com.yxl.smmall.product.service.PmsCategoryBrandRelationService;
import com.yxl.smmall.product.vo.BrandVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 品牌分类关联
 *
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-25 17:12:26
 */
@RestController
@RequestMapping("product/pmscategorybrandrelation")
public class PmsCategoryBrandRelationController {
    @Autowired
    private PmsCategoryBrandRelationService pmsCategoryBrandRelationService;

    /**
     * /product/pmscategorybrandrelation/brands/list
     */
    @GetMapping("/brands/list")
    public R relationBrandList(@RequestParam(value = "catId", required = true) Long catid) {
      List<PmsBrandEntity> vos =  pmsCategoryBrandRelationService.getBrandsBycatId(catid);
      //考虑到以后还会调用getBrandsBycatId这个方法，并不能保证返回的时BrandVo的类型，所以还是返回PmsBrandEntity
        Set<BrandVo> collect = vos.stream().map(item -> {
            BrandVo brandVo = new BrandVo();
//            由于BrandVo与PmsBrandEntity中的属性的类型不匹配，所以进行设置
            brandVo.setBrandId(item.getBrandId());
            brandVo.setBrandName(item.getName());
            return brandVo;
        }).collect(Collectors.toSet());
        return R.ok().put("data",collect);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:pmscategorybrandrelation:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = pmsCategoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 获取当前品牌列表
     */
    @RequestMapping(value = "/catelog/list", method = RequestMethod.GET)
    // @RequiresPermissions("product:pmscategorybrandrelation:list")
    public R cateloglist(@RequestParam("brandId") Long brandId) {
        List<PmsCategoryBrandRelationEntity> data =
                pmsCategoryBrandRelationService.list(
                        new QueryWrapper<PmsCategoryBrandRelationEntity>().
                                eq("brand_id", brandId));

        return R.ok().put("data", data);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:pmscategorybrandrelation:info")
    public R info(@PathVariable("id") Long id) {
        PmsCategoryBrandRelationEntity pmsCategoryBrandRelation = pmsCategoryBrandRelationService.getById(id);

        return R.ok().put("pmsCategoryBrandRelation", pmsCategoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //  @RequiresPermissions("product:pmscategorybrandrelation:save")
    public R save(@RequestBody PmsCategoryBrandRelationEntity pmsCategoryBrandRelation) {
        pmsCategoryBrandRelationService.saveDetail(pmsCategoryBrandRelation);
//TODO 更新其他关联信息

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("product:pmscategorybrandrelation:update")
    public R update(@RequestBody PmsCategoryBrandRelationEntity pmsCategoryBrandRelation){
		pmsCategoryBrandRelationService.updateById(pmsCategoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("product:pmscategorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids){
		pmsCategoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
