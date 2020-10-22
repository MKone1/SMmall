package com.yxl.smmall.product.fegin;

import com.yxl.common.utils.R;
import com.yxl.common.vo.SkuHasStockVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
/**由于返回值的类型难以匹配，解决方法：
 * 1，在R设计的时候可以加上泛型，实现泛型化
 * public class R<T> extends HashMap<String, Object>{
 *	private T data;
 * 	public T getData(){
 * 		return data;
 *        }
 *
 * 	public void setData(T data) {
 * 		this.data = data;
 *    }
 * }
 * R<List<ssss>> getSkusHasStock(@RequestBody List<Long> skuIds);
 * 即可
 *
 * 2，直接返回想要的结果类型
 * 3，封装解析结果
 */
@FeignClient("smmall-wares")
public interface WareFeginService {

    @PostMapping("/wares/wmswaresku/hasstock")
     R getSkusHasStock(@RequestBody List<Long> skuIdList);
}
