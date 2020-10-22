package com.yxl.smmall.product.fegin;

import com.yxl.common.to.es.SkuEsModel;
import com.yxl.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("smmall-search")
public interface SearchFeginService {

    @PostMapping("/search/save/product")
     R productSearch(@RequestBody List<SkuEsModel> skuEsModels);
}
