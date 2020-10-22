package com.yxl.smmall.product.app;

import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.R;
import com.yxl.smmall.product.entity.PmsAttrEntity;
import com.yxl.smmall.product.entity.PmsAttrGroupEntity;
import com.yxl.smmall.product.service.PmsAttrAttrgroupRelationService;
import com.yxl.smmall.product.service.PmsAttrGroupService;
import com.yxl.smmall.product.service.PmsAttrService;
import com.yxl.smmall.product.service.PmsCategoryService;
import com.yxl.smmall.product.vo.AttrGroupRelationVO;
import com.yxl.smmall.product.vo.AttrGroupWithAttrsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 属性分组
 *
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-25 17:12:26
 */
@RestController
@RequestMapping("product/pmsattrgroup")
public class PmsAttrGroupController {
    @Autowired
    private PmsAttrGroupService pmsAttrGroupService;
    @Autowired
    private PmsCategoryService pmsCategoryService;
    @Autowired
    private PmsAttrService pmsAttrService;
    @Autowired
    private PmsAttrAttrgroupRelationService pmsAttrAttrgroupRelationService;

    //    /product/attrgroup
    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttribute(@PathVariable("catelogId") Long catLogId) {
//        1,查出当前分类下的所有属性分组
//        2，查出每个属性分组的所有属性
     List<AttrGroupWithAttrsVO> vos =  pmsAttrGroupService.getAttrGroupWithByCatelogId(catLogId);
        return R.ok().put("data",vos);

    }


    ///product/attrgroup/attr/relation
    @PostMapping("/attr/relation")
    public R addRelation(@RequestBody List<AttrGroupRelationVO> vos) {

        pmsAttrService.saveBatch(vos);
        return R.ok();
    }


    /*
        /product/attrgroup/{attrgroupId}/attr/relation
        DATA:
        "attrId": 4,
          "attrName": "aad",
          "searchType": 1,
          "valueType": 1,
          "icon": "qq",
          "valueSelect": "v;q;w",
          "attrType": 1,
          "enable": 1,
          "catelogId": 225,
          "showDesc": 1

     */
    //获取分组关联属性，获取属性分组的关联的所有属性
    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrRalation(@PathVariable("attrgroupId") Long attgroupId) {
        List<PmsAttrEntity> list = pmsAttrService.getRelation(attgroupId);
        return R.ok().put("data", list);
    }


    //获取没有关联的所有属性

    /**
     * /product/attrgroup/{attrgroupId}/noattr/relation
     *
     * @param attgroupId
     * @return
     */
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R attrNoRalation(@RequestParam Map<String, Object> params
            , @PathVariable("attrgroupId") Long attgroupId) {
        PageUtils page = pmsAttrService.getNoRalation(params, attgroupId);

        return R.ok().put("data", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    // @RequiresPermissions("product:pmsattrgroup:list")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable("catelogId") Long catelogId) {
        PageUtils page = pmsAttrGroupService.queryPage(params, catelogId);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:pmsattrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId) {
        PmsAttrGroupEntity pmsAttrGroup = pmsAttrGroupService.getById(attrGroupId);
        Long catelogId = pmsAttrGroup.getCatelogId();
        Long[] pathArray = pmsCategoryService.findCatelogPath(catelogId);

        pmsAttrGroup.setCategoryPath(pathArray);

        return R.ok().put("pmsAttrGroup", pmsAttrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
  //  @RequiresPermissions("product:pmsattrgroup:save")
    public R save(@RequestBody PmsAttrGroupEntity pmsAttrGroup){
		pmsAttrGroupService.save(pmsAttrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("product:pmsattrgroup:update")
    public R update(@RequestBody PmsAttrGroupEntity pmsAttrGroup){
		pmsAttrGroupService.updateById(pmsAttrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:pmsattrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds) {
        pmsAttrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }
//    /product/pmsattrgroup/attr/relation/delete

    /**
     * 删除属性与分组的关联关系
     * 参数：[{"attrId":1,"attrGroupId":2}]
     */
    @RequestMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrGroupRelationVO[] vos) {
        pmsAttrService.deleteRelation(vos);
        return R.ok();
    }
}
