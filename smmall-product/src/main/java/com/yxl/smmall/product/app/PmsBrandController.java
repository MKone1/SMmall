package com.yxl.smmall.product.app;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.yxl.common.valid.AddGroup;
import com.yxl.common.valid.UpdataGroup;
import com.yxl.common.valid.UpdateStatusGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yxl.smmall.product.entity.PmsBrandEntity;
import com.yxl.smmall.product.service.PmsBrandService;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.R;


/**
 * 品牌
 *
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-25 17:12:26
 */
@RestController
@RequestMapping("product/pmsbrand")
public class PmsBrandController {
    @Autowired
    private PmsBrandService pmsBrandService;

    /**
     * 列表
     */
    @RequestMapping("/list/{brand}")
   // @RequiresPermissions("product:pmsbrand:list")
    public R listsome(@RequestParam Map<String, Object> params,@PathVariable("brand")Object brand){
//        PageUtils page = pmsBrandService.queryPage(params);
        PageUtils page = pmsBrandService.queryPage(params,brand);
        return R.ok().put("page", page);
    }
    @RequestMapping("/list")
    // @RequiresPermissions("product:pmsbrand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = pmsBrandService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    //@RequiresPermissions("product:pmsbrand:info")
    public R info(@PathVariable("brandId") Long brandId){
		PmsBrandEntity pmsBrand = pmsBrandService.getById(brandId);

        return R.ok().put("pmsBrand", pmsBrand);
    }

    /**
     * 保存
     * @Vlidated:
     * @Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
     * @Retention(RetentionPolicy.RUNTIME)
     * @Documented
     * public @interface Validated {
     *     Class<?>[] value() default {};
     * }
     * 指定一个或者多个校验分组，就会按照这些校验分组进行校验
     */
    @RequestMapping("/save")
  //  @RequiresPermissions("product:pmsbrand:save")
    public R save(@Validated(AddGroup.class) @RequestBody PmsBrandEntity pmsBrand, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            Map<String, String> map = new HashMap<>();
            bindingResult.getAllErrors().forEach((itemError)->{
//                获取到错误提示
                String defaultMessage = itemError.getDefaultMessage();
                String objectName = itemError.getObjectName();
                map.put(objectName,defaultMessage);
            });
        return    R.error(400,"提交数据不合法").put("data",map);
        }else {
            pmsBrandService.save(pmsBrand);
        }
        System.out.println(pmsBrand);
        pmsBrandService.save(pmsBrand);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("product:pmsbrand:update")
    public R update(@Validated(UpdataGroup.class)@RequestBody PmsBrandEntity pmsBrand){
		pmsBrandService.updateDetail(pmsBrand);

        return R.ok();
    }
    /**
     * 修改状态
     */
    @RequestMapping("/update/status")
    // @RequiresPermissions("product:pmsbrand:update")
    public R updateStatus(@Validated(UpdateStatusGroup.class)@RequestBody PmsBrandEntity pmsBrand){
        pmsBrandService.updateById(pmsBrand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("product:pmsbrand:delete")
    public R delete(@RequestBody Long[] brandIds){
		pmsBrandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
