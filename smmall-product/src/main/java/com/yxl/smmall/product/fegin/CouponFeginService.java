package com.yxl.smmall.product.fegin;

import com.yxl.common.to.SkuReductionTO;
import com.yxl.common.to.SpuBoundTO;
import com.yxl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("smmall-coupon")
public interface CouponFeginService {
    /**
     * 1,CouponFeginService.saveSpuBounds(SpuBoundTO)
     *      1),@RequestBody将对象转为Json
     *      2),找到smmall-coupon服务给/coupon/smsspubounds/save发送请求，
     *          将上一步的json数据放在请求体的位置，发送请求；
     *      3），对方服务收到请求，请求体里有Json数据
     *      @RequestBody  SmsSpuBoundsEntity smsSpuBounds ；将请求体中的数据转为SmsSpuBoundsEntity
     *
     * 只要json数据模型是兼容的，双方服务无需使用同一个TO
     *
     * @param spuBroundTO
     * @return
     */
    @PostMapping("/coupon/smsspubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTO spuBroundTO);
    @PostMapping("/coupon/smsskufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTO skuReductionTO);
}
