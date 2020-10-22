package com.yxl.smmall.lcart.server;


import com.yxl.smmall.lcart.vo.CartItem;
import com.yxl.smmall.lcart.vo.CartVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author SADSADSD
 */
@Service
public interface CartService {
    CartItem addToCart(Long skuId, Integer number) throws ExecutionException, InterruptedException;

    CartItem getCartItem(Long skuId);

   CartVo getCart() throws ExecutionException, InterruptedException;

    void clearCart(String userkey);

    void checkItem(Long skuId, Integer checked);

    void updataCount(Long skuid, Integer number);

    void delete(Long skuid);


    List<CartItem> getCartItemsToOrder();
}
