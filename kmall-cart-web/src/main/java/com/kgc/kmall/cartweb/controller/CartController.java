package com.kgc.kmall.cartweb.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kgc.kmall.bean.OmsCartItem;
import com.kgc.kmall.bean.PmsSkuInfo;
import com.kgc.kmall.service.CartService;
import com.kgc.kmall.service.SkuService;
import com.kgc.kmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author shkstart
 * @create 2021-01-08 14:22
 */
@Controller
public class CartController {
    @Reference
    SkuService skuService;
    @Reference
    CartService cartService;
    @RequestMapping("/addToCart")
    public String addToCart(long skuId, Integer num, HttpServletRequest request, HttpServletResponse response){
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        // 调用商品服务查询商品信息
        PmsSkuInfo skuInfo = skuService.selectBySkuId(skuId);
        //将商品信息封装成购物车信息
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setDeleteStatus(0);
        omsCartItem.setModifyDate(new Date());
        omsCartItem.setPrice(new BigDecimal(skuInfo.getPrice()));
        omsCartItem.setProductAttr("");
        omsCartItem.setProductBrand("");
        omsCartItem.setProductCategoryId(skuInfo.getCatalog3Id());
        omsCartItem.setProductId(skuInfo.getSpuId());
        omsCartItem.setProductName(skuInfo.getSkuName());
        omsCartItem.setProductPic(skuInfo.getSkuDefaultImg());
        omsCartItem.setProductSkuCode("11111111111");
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setQuantity(num);

        // 判断用户是否登录
        String memberId = "555";

        if (StringUtils.isBlank(memberId)) {
            // cookie里原有的购物车数据
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if (StringUtils.isBlank(cartListCookie)) {
                // cookie为空
                omsCartItems.add(omsCartItem);
            }else{
                // cookie不为空
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
                // 判断添加的购物车数据在cookie中是否存在
                boolean exist = if_cart_exist(omsCartItems, omsCartItem);
                if (exist) {
                    // 之前添加过，更新购物车添加数量
                    for (OmsCartItem cartItem : omsCartItems) {
                        if (cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())) {
                            cartItem.setQuantity(cartItem.getQuantity()+omsCartItem.getQuantity());
                            break;
                        }
                    }
                }else {
                    // 之前没有添加，新增当前的购物车
                    omsCartItems.add(omsCartItem);
                }
            }
            // 更新cookie
            CookieUtil.setCookie(request, response, "cartListCookie", JSON.toJSONString(omsCartItems), 60 * 60 * 72, true);
        }else {
            OmsCartItem omsCartItemFromDb = cartService.ifCartExistByUser(memberId,skuId);
            if(omsCartItemFromDb==null){
                // 该用户没有添加过当前商品
                omsCartItem.setMemberId(Long.parseLong(memberId));
                omsCartItem.setMemberNickname("test小明");
                cartService.addCart(omsCartItem);
            }else{
                // 该用户添加过当前商品
                Integer quantity = omsCartItemFromDb.getQuantity();
                quantity = quantity + num;
                omsCartItemFromDb.setQuantity(quantity);
                cartService.updateCart(omsCartItemFromDb);
            }
            // 同步缓存
            cartService.flushCartCache(memberId);
        }
        return "redirect:/success.html";
    }

    /**
     * 判断cookie中是否存在
     * @param omsCartItems
     * @param omsCartItem
     * @return
     */
    private boolean if_cart_exist(List<OmsCartItem> omsCartItems, OmsCartItem omsCartItem) {

        boolean b = false;
        //循环查找集合中的所有数据
        for (OmsCartItem cartItem : omsCartItems) {
            ///获取每条数据的SkuId
            Long productSkuId = cartItem.getProductSkuId();
            //用集合的SkuId和要添加的skuId作比对
            if (productSkuId.equals(omsCartItem.getProductSkuId())) {
                b = true;
                break;
            }
        }

        return b;
    }
    @RequestMapping("/cartList")
    public String cartList(ModelMap modelMap, HttpServletRequest request){
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        String memberId = "";

        if(StringUtils.isNotBlank(memberId)){
            // 已经登录查询db
            omsCartItems = cartService.cartList(memberId);
        }else{
            // 没有登录查询cookie
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if(StringUtils.isNotBlank(cartListCookie)){
                omsCartItems = JSON.parseArray(cartListCookie,OmsCartItem.class);
            }
        }

        modelMap.put("cartList",omsCartItems);
        return "cartList";
    }
}
