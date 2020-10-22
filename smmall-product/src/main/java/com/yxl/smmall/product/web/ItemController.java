package com.yxl.smmall.product.web;

import com.yxl.smmall.product.service.PmsSkuInfoService;
import com.yxl.smmall.product.vo.SkuInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 商品详情页
 *
 * @author SADSADSD
 */
@Controller
public class ItemController {
    @Autowired
    PmsSkuInfoService skuInfoService;

    /*实现对商品详情页的搜索
     *
     * @param skuId 商品详情ID
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String itemPageUrl(@PathVariable Long skuId , Model model) {
        System.out.println("skuId" + skuId);
        SkuInfoVo skuInfoVo = new SkuInfoVo();
        try{
      skuInfoVo = skuInfoService.item((skuId));
        }catch (Exception e){
            e.printStackTrace();
        }

    model.addAttribute("item",skuInfoVo);
        System.out.println(skuInfoVo);
        return "item";
    }
}
