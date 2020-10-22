package com.yxl.smmall.lcart.controller;

import com.yxl.common.constant.CartConstant;
import com.yxl.smmall.lcart.interceptor.CartInterceptor;
import com.yxl.smmall.lcart.server.CartService;
import com.yxl.smmall.lcart.to.UserInfoTo;
import com.yxl.smmall.lcart.vo.CartItem;
import com.yxl.smmall.lcart.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 购物页面展示
 *
 * @author SADSADSD
 */
@Controller
public class CartListController {
    @Autowired
    CartService cartService;

    /**
     * 为了远程服务
     * @return
     */
    @GetMapping("/getItems")
    @ResponseBody
    public List<CartItem> CartItems(){
        List<CartItem> cartItemList = cartService.getCartItemsToOrder();
        return cartItemList;
    }

    @GetMapping("/cartList.html")
    public String cartListUrl(Model model) throws ExecutionException, InterruptedException {
        //线程共享
        CartVo cartVo = cartService.getCart();
        model.addAttribute("list", cartVo);
        return "cartList";
    }

    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer number, RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
        System.out.println("skuid" + skuId + "num" + number);
        cartService.addToCart(skuId, number);
        redirectAttributes.addAttribute("skuId", skuId);
        return "redirect:http://cart.smmall.com//addToCartSuccess.html";
    }

    /**
     * 为了防止单刷页面过多的提交商品的数量，再处理完添加之后重定向到另一个网页
     *
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId, Model model) {
        CartItem cartItem = cartService.getCartItem(skuId);
        model.addAttribute("item", cartItem);
        return "success";
    }

    /**
     * 处理修改是否被选中
     *
     * @param skuId
     * @param checked
     * @return
     */
    @GetMapping("/checkedItem")
    public String updateChecked(@RequestParam("skuId") Long skuId, @RequestParam("checked") Integer checked) {
        cartService.checkItem(skuId, checked);
        return "redirect:http://cart.smmall.com/cartList.html";
    }

    /**
     * 修改购物车商品的数量
     * @param skuid
     * @param number
     * @return
     */
    @GetMapping("/countNum")
    public String updataItemCount(@RequestParam("skuId") Long skuid, @RequestParam("num") Integer number) {
        cartService.updataCount(skuid,number);
        return "redirect:http://cart.smmall.com/cartList.html";
    }

    /**
     * 删除购物车选项
     * @param skuid
     * @return
     */
    @GetMapping("/deleteCart")
    public String deleteCart(@RequestParam("skuId") Long skuid){
        cartService.delete(skuid);
        return "redirect:http://cart.smmall.com/cartList.html";
    }

}
