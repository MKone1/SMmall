package com.yxl.smmall.lcart.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 整个购物车
 * 需要计算的属性，必须重写get方法，保证每一次获取属性都会计算
 */
public class CartVo  {
    List<CartItem> items; //购物项集合
    private  Integer countNum; //商品数量
    private Integer countType; //商品类型数量
    private BigDecimal totalAmount; //商品总价
    private BigDecimal reduce = new BigDecimal("0.00"); //优惠价格

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public Integer getCountNum() {
        int count = 0;
        if (items != null && items.size()>0){
            for (CartItem item:items){
                count+=item.getCount();
            }
        }
        return count;
    }



    public Integer getCountType() {

        int count = 0;
        if (items != null && items.size()>0){
            for (CartItem item:items){
                count+=1;
            }
        }
        return count;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal bigDecimal = new BigDecimal("0");
        // 1,计算所有购物项的总价
        if (items != null && items.size()>0){
            for (CartItem item :items){
                if(item.getChecked()){
                    BigDecimal totalPrice = item.getTotalPrice();
                    bigDecimal = bigDecimal.add(totalPrice);
                }

            }
        }
        //2，减掉优惠信息的价格
        BigDecimal subtract = bigDecimal.subtract(getReduce());
        return subtract;

    }



    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
