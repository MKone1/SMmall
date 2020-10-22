package com.yxl.smmall.product.app;

import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.R;
import com.yxl.smmall.product.entity.PmsSkuSaleAttrValueEntity;
import com.yxl.smmall.product.service.PmsSkuSaleAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * sku销售属性&值
 *
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-25 17:12:26
 */
@RestController
@RequestMapping("product/pmsskusaleattrvalue")
public class PmsSkuSaleAttrValueController {
    @Autowired
    private PmsSkuSaleAttrValueService pmsSkuSaleAttrValueService;

    @GetMapping("/stringList/{skuid}")
    public List<String> getSkuList(@PathVariable("skuid") Long SkuId) {
      return   pmsSkuSaleAttrValueService.getSkuSaleAttrValueAsStringList(SkuId);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:pmsskusaleattrvalue:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = pmsSkuSaleAttrValueService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:pmsskusaleattrvalue:info")
    public R info(@PathVariable("id") Long id){
		PmsSkuSaleAttrValueEntity pmsSkuSaleAttrValue = pmsSkuSaleAttrValueService.getById(id);

        return R.ok().put("pmsSkuSaleAttrValue", pmsSkuSaleAttrValue);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
  //  @RequiresPermissions("product:pmsskusaleattrvalue:save")
    public R save(@RequestBody PmsSkuSaleAttrValueEntity pmsSkuSaleAttrValue){
		pmsSkuSaleAttrValueService.save(pmsSkuSaleAttrValue);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("product:pmsskusaleattrvalue:update")
    public R update(@RequestBody PmsSkuSaleAttrValueEntity pmsSkuSaleAttrValue){
		pmsSkuSaleAttrValueService.updateById(pmsSkuSaleAttrValue);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("product:pmsskusaleattrvalue:delete")
    public R delete(@RequestBody Long[] ids){
		pmsSkuSaleAttrValueService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
