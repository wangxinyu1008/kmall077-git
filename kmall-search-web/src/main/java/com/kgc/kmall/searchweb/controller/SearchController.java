package com.kgc.kmall.searchweb.controller;

import com.kgc.kmall.bean.PmsBaseAttrValue;
import com.kgc.kmall.bean.PmsSearchSkuInfo;
import com.kgc.kmall.bean.PmsSearchSkuParam;
import com.kgc.kmall.bean.PmsSkuAttrValue;
import com.kgc.kmall.service.SearchService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author shkstart
 * @create 2021-01-04 17:12
 */
@Controller
public class SearchController {
    @Reference
    SearchService searchService;
    @RequestMapping("/index.html")
    public String index(){        return "index";}

    @RequestMapping("/list.html")
    public String list(PmsSearchSkuParam pmsSearchSkuParam, Model model){
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = searchService.list(pmsSearchSkuParam);
        model.addAttribute("skuLsInfoList",pmsSearchSkuInfos);
        //获取平台属性valueId
        Set<Long> valueIdSet = new HashSet<>();

        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            for (int i = 0; i < pmsSearchSkuInfo.getSkuAttrValueList().size(); i++) {
                Map<Object,Object> pmsSkuAttrValue =(Map<Object, Object>) pmsSearchSkuInfo.getSkuAttrValueList().get(i);
                valueIdSet.add(Long.parseLong(pmsSkuAttrValue.get("valueId").toString()));
            }
        }
        System.out.println(valueIdSet);
        return "list";
    }
}
