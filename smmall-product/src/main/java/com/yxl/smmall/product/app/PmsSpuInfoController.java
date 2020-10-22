package com.yxl.smmall.product.app;

import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.R;
import com.yxl.smmall.product.entity.PmsSpuInfoEntity;
import com.yxl.smmall.product.service.PmsSpuInfoService;
import com.yxl.smmall.product.vo.SpuSaveVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;



/**
 * spu信息
 *
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-25 17:12:26
 */
@RestController
@RequestMapping("product/pmsspuinfo")
public class PmsSpuInfoController {
    @Autowired
    private PmsSpuInfoService pmsSpuInfoService;

    /**
     * 拦截商家服务
     * product/pmsspuinfo/{spuId}/up
     */
    @PostMapping("/{spuId}/up")
    public R spuUp(@PathVariable("spuId") Long spuId) {
        pmsSpuInfoService.up(spuId);
        return R.ok();
    }

    @GetMapping("/skuId/{id}")
    public R getSpuInfoBySkuId(@PathVariable("id") Long skuId) {
        PmsSpuInfoEntity spuInfoEntity = pmsSpuInfoService.getSpuInfoBySkuId(skuId);
        return R.ok().setData(spuInfoEntity);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:pmsspuinfo:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = pmsSpuInfoService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:pmsspuinfo:info")
    public R info(@PathVariable("id") Long id){
		PmsSpuInfoEntity pmsSpuInfo = pmsSpuInfoService.getById(id);

        return R.ok().put("pmsSpuInfo", pmsSpuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //  @RequiresPermissions("product:pmsspuinfo:save")
    public R save(@RequestBody SpuSaveVO saveVO) {
//		pmsSpuInfoService.save(saveVO);
        pmsSpuInfoService.saveSpuInfo(saveVO);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("product:pmsspuinfo:update")
    public R update(@RequestBody PmsSpuInfoEntity pmsSpuInfo){
		pmsSpuInfoService.updateById(pmsSpuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("product:pmsspuinfo:delete")
    public R delete(@RequestBody Long[] ids){
		pmsSpuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
