package com.yxl.smmall.lcart.server.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yxl.common.constant.CartConstant;
import com.yxl.common.utils.R;
import com.yxl.smmall.lcart.fegin.AddToCartFegin;
import com.yxl.smmall.lcart.interceptor.CartInterceptor;
import com.yxl.smmall.lcart.server.CartService;
import com.yxl.smmall.lcart.to.UserInfoTo;
import com.yxl.smmall.lcart.vo.CartItem;
import com.yxl.smmall.lcart.vo.CartVo;
import com.yxl.smmall.lcart.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author SADSADSD
 */
@Service
@Slf4j
public class CartServiceImpl implements CartService {
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    AddToCartFegin addToCartFegin;
    @Autowired
    ThreadPoolExecutor threadPoolExecutor;


    /**
     * 该方法涉及到多个远程服务的请求，需要采用多线程异步编排的方式
     *
     * @param skuId
     * @param number
     * @return
     */
    @Override
    public CartItem addToCart(Long skuId, Integer number) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        String result = (String) cartOps.get(skuId.toString());
        if (StringUtils.isEmpty(result)) {
            //说明是第一次添加该商品

//        调用远程服务查询出当前商品的详细信息
            CartItem cartItem = new CartItem();
            CompletableFuture<Void> pmsSkuInfo = CompletableFuture.runAsync(() -> {
                R info = addToCartFegin.info(skuId);
                SkuInfoVo skuInfo = info.getData("pmsSkuInfo", new TypeReference<SkuInfoVo>() {
                });
                cartItem.setChecked(true);
                cartItem.setCount(number);
                cartItem.setTitle(skuInfo.getSkuTitle());
                cartItem.setPrice(skuInfo.getPrice());
                cartItem.setImageUrl(skuInfo.getSkuDefaultImg());
                cartItem.setSkuId(skuId);
            }, threadPoolExecutor);

            CompletableFuture<Void> getskuSaleAttrList = CompletableFuture.runAsync(() -> {
                //设置展示属性，远程调用服务查询
                List<String> skuList = addToCartFegin.getSkuList(skuId);

                cartItem.setSkuAttr(skuList);
            }, threadPoolExecutor);
            //由于异步编排的原因，当前两个执行异步完成之后在执行，
            //当执行完成之后，通过get方法阻塞线程
            CompletableFuture.allOf(getskuSaleAttrList, pmsSkuInfo).get();
            String s = JSON.toJSONString(cartItem);
            getCartOps().put(skuId.toString(), s);
            return cartItem;
        } else {
            //说明已经添加过该商品，只需要修改商品的数量
            CartItem cartItem = JSON.parseObject(result, CartItem.class);
            cartItem.setCount(cartItem.getCount() + number);
            cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
            return cartItem;
        }


    }

    /**
     * 查询某一个具体的购物项
     *
     * @param skuId
     * @return
     */
    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        String resultString = (String) cartOps.get(skuId.toString());
        CartItem cartItem = JSON.parseObject(resultString, CartItem.class);
        return cartItem;


    }

    /**
     * 实现获取购物车信息，并执行合并购物车
     *
     * @return
     */
    @Override
    public CartVo getCart() throws ExecutionException, InterruptedException {
        CartVo item = new CartVo();
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserid() != null) {
            //登陆,如果登陆之后，临时用户中还有商品，将临时用户和登陆用户购物车进行合并
            String userid = CartConstant.CART_PREFIX + userInfoTo.getUserid();
            //查询出登陆用户的购物车
            List<CartItem> cartItems = getCartItems(CartConstant.CART_PREFIX + userInfoTo.getUserkey());
            if (cartItems != null) {
                //临时用户购物车有商品，合并
                for (CartItem cartItem : cartItems) {
                    addToCart(cartItem.getSkuId(), cartItem.getCount());
                }
                //合并完成后，清空临时购物车
                clearCart(CartConstant.CART_PREFIX + userInfoTo.getUserkey());
            }
            //合并完成后获取购物车的数据
            List<CartItem> cartItemList = getCartItems(userid);
            item.setItems(cartItemList);
        } else {
            //表示没有登陆
            String userkey = CartConstant.CART_PREFIX + userInfoTo.getUserkey();
            List<CartItem> collect = getCartItems(userkey);
            item.setItems(collect);
        }
        return item;

    }

    /**
     * 清空临时购物车
     *
     * @param userkey
     */
    @Override
    public void clearCart(String userkey) {

        redisTemplate.delete(userkey);

    }

    @Override
    public void checkItem(Long skuId, Integer checked) {
        BoundHashOperations<String, Object, Object> cartOps =
                getCartOps();
        CartItem cartItem = getCartItem(skuId);
        cartItem.setChecked(checked == 1 ? true : false);
        String s = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(), s);
    }

    @Override
    public void updataCount(Long skuid, Integer number) {
        BoundHashOperations<String, Object, Object> cartOps =
                getCartOps();
        CartItem cartItem = getCartItem(skuid);
        cartItem.setCount(number);
        String s = JSON.toJSONString(cartItem);
        cartOps.put(skuid.toString(), s);

    }

    @Override
    public void delete(Long skuid) {
        BoundHashOperations<String, Object, Object> cartOps =
                getCartOps();
        cartOps.delete(skuid.toString());

    }


    public List<CartItem> getCartItems(String userid) {
        BoundHashOperations<String, Object, Object> hashOps =
                redisTemplate.boundHashOps(userid);
        List<Object> values = hashOps.values();
        if (values != null && values.size() > 0) {
            List<CartItem> collect = values.stream().map((item) -> {
                String items = (String) item;
                CartItem cartItem = JSON.parseObject(items, CartItem.class);
                return cartItem;
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    /**
     * 查询当前用户被选的购物项，并实时查询价格
     *
     * @return
     */
    @Override
    public List<CartItem> getCartItemsToOrder() {
        Long userid = CartInterceptor.threadLocal.get().getUserid();
        String s = CartConstant.CART_PREFIX + userid;
        List<CartItem> cartItem = getCartItems(s);
        List<CartItem> collect = cartItem.stream()
                .filter(item -> item.getChecked())
                .map(item -> {
                    R info = addToCartFegin.info(item.getSkuId());
                    //TODO:现在没有这个商品，以及这个商品的数量为0了
                    if (info == null) {
                        return null;
                    } else {
                        //更新最新价格
                        SkuInfoVo pmsSkuInfo = info.getData("pmsSkuInfo", new TypeReference<SkuInfoVo>() {
                        });

                        item.setPrice(pmsSkuInfo.getPrice());
                        return item;
                    }

                })
                .collect(Collectors.toList());

        return collect;
    }

    /**
     * 获取到需要操作的购物车
     *
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        //1，获取到用户的信息，判断是否登陆，是否是临时用户
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String userkey = "";
        if (userInfoTo.getUserid() != null) {
//表示已经登陆
            userkey = CartConstant.CART_PREFIX + userInfoTo.getUserid();
        } else {
            //表示没有登陆，使用临时用户
            userkey = CartConstant.CART_PREFIX + userInfoTo.getUserkey();
        }
        //在Redis缓存中存储购物车信息
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(userkey);
        return operations;
    }


}


