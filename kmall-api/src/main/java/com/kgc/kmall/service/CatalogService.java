package com.kgc.kmall.service;

import com.kgc.kmall.bean.PmsBaseCatalog1;
import com.kgc.kmall.bean.PmsBaseCatalog2;
import com.kgc.kmall.bean.PmsBaseCatalog3;

import java.util.List;

/**
 * @author shkstart
 * @create 2020-12-16 15:21
 */
public interface CatalogService {
    List<PmsBaseCatalog1> getCatalog1();
    List<PmsBaseCatalog2> getCatalog2(Integer catalog1Id);
    List<PmsBaseCatalog3> getCatalog3(Long catalog2Id);
}
