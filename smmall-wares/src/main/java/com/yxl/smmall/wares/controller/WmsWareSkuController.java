package com.yxl.smmall.wares.controller;

import com.yxl.common.exception.BizCodeEnume;
import com.yxl.common.utils.PageUtils;
import com.yxl.common.utils.R;
import com.yxl.common.vo.SkuHasStockVO;
import com.yxl.common.vo.WareSkuLockVo;
import com.yxl.smmall.wares.entity.WmsWareSkuEntity;
import com.yxl.smmall.wares.excrption.NoStockException;
import com.yxl.smmall.wares.service.WmsWareSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 商品库存
 *
 * @author yxl
 * @email 1326032159@qq.com
 * @date 2020-06-18 09:52:15
 */
@RestController
@RequestMapping("wares/wmswaresku")
public class WmsWareSkuController {
    @Autowired
    private WmsWareSkuService wmsWareSkuService;

    //查询Sku是否有库存，通过调用远程服务
    @PostMapping("/hasstock")
    public R getSkusHasStock(@RequestBody List<Long> skuIdList) {
        System.out.println("skuIdList:" + skuIdList.size());
        //sku_id ,stock
        List<SkuHasStockVO> vos = wmsWareSkuService.getSkusHasStock(skuIdList);

        return R.ok().setData(vos);
    }

    /**
     * 锁库存服务
     *
     * @param vo
     * @return
     */
    @PostMapping("/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockVo vo) {
        try{
            Boolean lock = wmsWareSkuService.orderlockstock(vo);
            return R.ok();
        }catch (NoStockException e){
            return R.error(BizCodeEnume.ITEM_OF_WARES_EXCEPTION.getCode(),BizCodeEnume.ITEM_OF_WARES_EXCEPTION.getMsg());
        }



    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("wares:wmswaresku:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = wmsWareSkuService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("wares:wmswaresku:info")
    public R info(@PathVariable("id") Long id){
		WmsWareSkuEntity wmsWareSku = wmsWareSkuService.getById(id);

        return R.ok().put("wmsWareSku", wmsWareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
  //  @RequiresPermissions("wares:wmswaresku:save")
    public R save(@RequestBody WmsWareSkuEntity wmsWareSku){
		wmsWareSkuService.save(wmsWareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("wares:wmswaresku:update")
    public R update(@RequestBody WmsWareSkuEntity wmsWareSku){
		wmsWareSkuService.updateById(wmsWareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("wares:wmswaresku:delete")
    public R delete(@RequestBody Long[] ids){
		wmsWareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
