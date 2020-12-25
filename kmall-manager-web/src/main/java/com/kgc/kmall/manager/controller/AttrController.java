package com.kgc.kmall.manager.controller;

import com.kgc.kmall.bean.PmsBaseAttrInfo;
import com.kgc.kmall.bean.PmsBaseAttrValue;
import com.kgc.kmall.service.AttrService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author shkstart
 * @create 2020-12-16 16:16
 */
@CrossOrigin
@RestController
@Api(tags = "平台属性",description = "提供平台属性")
public class AttrController {
    @Reference
    AttrService attrService;
    @ApiOperation(value = "根据3级分类id查询平台属性所有信息",httpMethod = "GET")
    @RequestMapping("/attrInfoList")
    @ApiImplicitParam(name = "catalog3Id",value = "3级分类id")
    public List<PmsBaseAttrInfo> attrInfoList(Long catalog3Id){
        List<PmsBaseAttrInfo> infoList = attrService.select(catalog3Id);
        return infoList;
    }
    @ApiOperation(value = "添加平台属性",httpMethod = "POST")
    @RequestMapping("/saveAttrInfo")
    @ApiImplicitParam(name = "attrInfo",value = "平台属性")
    public Integer saveAttrInfo(@RequestBody PmsBaseAttrInfo attrInfo){
        Integer i = attrService.add(attrInfo);
        return i;
    }
    @ApiOperation(value = "根据属性id查询对应的属性值",httpMethod = "GET")
    @RequestMapping("/getAttrValueList")
    @ApiImplicitParam(name = "attrId",value = "属性id")
    public List<PmsBaseAttrValue> getAttrValueList(Long attrId){
        List<PmsBaseAttrValue> valueList = attrService.getAttrValueList(attrId);
        return valueList;
    }
}
