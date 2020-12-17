package com.kgc.kmall.service;

import com.kgc.kmall.bean.PmsBaseSaleAttr;
import com.kgc.kmall.bean.PmsProductInfo;

import java.util.List;

/**
 * @author shkstart
 * @create 2020-12-17 13:31
 */
public interface SpuService {
    List<PmsProductInfo> spuList(Long catalog3Id);
    List<PmsBaseSaleAttr> baseSaleAttrList();
}
