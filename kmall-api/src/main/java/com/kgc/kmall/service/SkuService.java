package com.kgc.kmall.service;

import com.kgc.kmall.bean.PmsSkuInfo;

import java.util.List;

/**
 * @author shkstart
 * @create 2020-12-23 19:14
 */
public interface SkuService {
    String saveSkuInfo(PmsSkuInfo skuInfo);
    PmsSkuInfo selectBySkuId(Long id);
    List<PmsSkuInfo> selectBySpuId(Long spuId);
    List<PmsSkuInfo> getAllSku();
}
