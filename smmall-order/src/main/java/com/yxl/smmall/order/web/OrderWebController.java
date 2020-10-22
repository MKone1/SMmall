package com.yxl.smmall.order.web;

import com.yxl.common.vo.CheckOutPageVO;
import com.yxl.common.vo.OrderSubmitVO;
import com.yxl.smmall.order.service.OmsOrderService;
import com.yxl.smmall.order.vo.SubmitOrderResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;

/**
 * @author SADSADSD
 */
@Controller
public class OrderWebController {
    @Autowired
    OmsOrderService orderService;

    @GetMapping("order.html")
    public String orderPage() {
        return "order";
    }

    /**
     * 订单结算页面
     *
     * @return
     */
    @GetMapping("checkOut.html")
    public String checkOutPage(Model model, HttpServletRequest request) throws ExecutionException, InterruptedException {
        CheckOutPageVO checkOutPageVOList = orderService.checkOut();
        model.addAttribute("orderCheckOut", checkOutPageVOList);
        return "checkOut";
    }

    /**
     * 下单功能
     *
     * @param vo
     * @return
     */
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVO vo,Model model) {
        //下单，创建订单，验证令牌，验价，锁库存。。。。。。。
        System.out.println("ordersubmitdata"+vo);
        SubmitOrderResponseVO submit = orderService.submitOrder(vo);
        if (submit.getCode() == 0){
            //下单成功来到支付页面
            model.addAttribute("submitOrderResponseVO",submit);
            return "waitPay";
        }else{
            //下单失败回到订单页面重新确认订单信息
            return "redirect:http://order.smmall.com/checkOut.html";
        }

    }



    @GetMapping("cashier.html")
    public String cashierPage() {
        return "cashier";
    }

    @GetMapping("waitPay.html")
    public String waitPayPage() {
        return "waitPay";
    }


}
