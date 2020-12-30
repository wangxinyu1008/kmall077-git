package com.kgc.kmall.itemweb.controller;

import com.kgc.kmall.bean.PmsBaseSaleAttr;
import com.kgc.kmall.bean.PmsProductSaleAttr;
import com.kgc.kmall.bean.PmsSkuInfo;
import com.kgc.kmall.service.SkuService;
import com.kgc.kmall.service.SpuService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author shkstart
 * @create 2020-12-29 14:02
 */
@Controller
public class ItemController {
    @Reference
    SkuService skuService;
    @Reference
    SpuService spuService;
    @RequestMapping("{skuId}.html")
    public String item(@PathVariable Long skuId, Model model){
        PmsSkuInfo pmsSkuInfo = skuService.selectBySkuId(skuId);
        //根据spuid获取销售属性和属性和属性值
        List<PmsBaseSaleAttr> pmsBaseSaleAttrs = spuService.spuSaleAttrListIsCheck(pmsSkuInfo.getSpuId(), pmsSkuInfo.getId());
        model.addAttribute("spuSaleAttrListCheckBySku",pmsBaseSaleAttrs);
        model.addAttribute("skuInfo",pmsSkuInfo);
        return "item";
    }
}
