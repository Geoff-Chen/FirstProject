package com.pinyougou.cart.service;

import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojogroup.Cart;

import java.util.List;

public interface CartService {

    /**
     * 添加商品到购物车列表集合中
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num );

    /**
     * 从Redis中查询购物车列表集合
     * @param username
     * @return
     */
    public List<Cart> findCartListFromRedis(String username);

    /**
     * 保存购物车列表集合到Redis中
     * @param username
     * @param cartList
     */
    public void saveCartListToRedis(String username,List<Cart> cartList);


    /**
     * 合并购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
    public List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);


}
