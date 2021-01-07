package com.kgc.kmall.searchweb.controller;

import com.kgc.kmall.bean.*;
import com.kgc.kmall.service.AttrService;
import com.kgc.kmall.service.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

/**
 * @author shkstart
 * @create 2021-01-04 17:12
 */
@Controller
public class SearchController {
    @Reference
    SearchService searchService;
    @Reference
    AttrService attrService;
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
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrService.selectAttrInfoValueListByValueId(valueIdSet);
        model.addAttribute("attrList",pmsBaseAttrInfos);
        String[] valueId = pmsSearchSkuParam.getValueId();
        //封装面包屑属性
        if(valueId!=null){
            List<PmsSearchCrumb> pmsSearchCrumbs=new ArrayList<>();
            for (String s : valueId) {
                PmsSearchCrumb pmsSearchCrumb=new PmsSearchCrumb();
                pmsSearchCrumb.setValueId(s);
                String valueName=getValueName(pmsBaseAttrInfos,s);
                pmsSearchCrumb.setValueName(valueName);
                pmsSearchCrumb.setUrlParam(getURLParam(pmsSearchSkuParam,s));
                pmsSearchCrumbs.add(pmsSearchCrumb);
            }
            model.addAttribute("attrValueSelectedList", pmsSearchCrumbs);

        }
        //删除平台属性
        if(valueId!=null) {
            Iterator<PmsBaseAttrInfo> iterator = pmsBaseAttrInfos.iterator();
            while (iterator.hasNext()) {
                PmsBaseAttrInfo next = iterator.next();
                for (PmsBaseAttrValue pmsBaseAttrValue : next.getAttrValueList()) {
                    for (String s : valueId) {
                        if (s.equals(pmsBaseAttrValue.getId().toString())) {
                            iterator.remove();
                        }
                    }
                }
            }
        }
        //显示关键字
        model.addAttribute("keyword",pmsSearchSkuParam.getKeyword());
        //拼接平台属性URL
        String urlParam=getURLParam(pmsSearchSkuParam);
        model.addAttribute("urlParam",urlParam);

        return "list";
    }

    /**
     * 根据条件对象拼接URL
     * @return
     */
    public String getURLParam(PmsSearchSkuParam pmsSearchSkuParam){
        StringBuffer stringBuffer=new StringBuffer();
        String keyword=pmsSearchSkuParam.getKeyword();
        String catalog3Id=pmsSearchSkuParam.getCatalog3Id();
        String[] valueId = pmsSearchSkuParam.getValueId();
        if(StringUtils.isNotBlank(keyword)){
            stringBuffer.append("&keyword="+keyword);
        }
        if(StringUtils.isNotBlank(catalog3Id)){
            stringBuffer.append("&catalog3Id="+catalog3Id);
        }
        if (valueId!=null){
            for (String vid : valueId) {
                stringBuffer.append("&valueId="+vid);
            }
        }
        return stringBuffer.substring(1);
    }
    /**
     * 拼接面包屑的URL,面包屑对应的urlParam=当前URL中的valueId-面包屑的valueId
     * @return
     */
    public String getURLParam(PmsSearchSkuParam pmsSearchSkuParam,String vid){
        StringBuffer stringBuffer=new StringBuffer();
        String keyword=pmsSearchSkuParam.getKeyword();
        String catalog3Id=pmsSearchSkuParam.getCatalog3Id();
        String[] valueId = pmsSearchSkuParam.getValueId();
        if(StringUtils.isNotBlank(keyword)){
            stringBuffer.append("&keyword="+keyword);
        }
        if(StringUtils.isNotBlank(catalog3Id)){
            stringBuffer.append("&catalog3Id="+catalog3Id);
        }
        if (valueId!=null){
            for (String id : valueId) {
                if(vid.equals(id)==false) {
                    stringBuffer.append("&valueId=" + id);
                }
            }
        }
        return stringBuffer.substring(1);
    }

    /**
     * 显示平台属性值
     * @return
     */
    public String getValueName(List<PmsBaseAttrInfo> pmsBaseAttrInfos,String valueId){
        String valueName="";
        for (PmsBaseAttrInfo pmsBaseAttrInfo : pmsBaseAttrInfos) {
            for (PmsBaseAttrValue pmsBaseAttrValue : pmsBaseAttrInfo.getAttrValueList()) {
                if(valueId.equals(pmsBaseAttrValue.getId().toString())){
                    valueId=pmsBaseAttrInfo.getAttrName()+"："+pmsBaseAttrValue.getValueName();
                    break;
                }
            }
        }
        return valueId;
    }
}
