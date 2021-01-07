package com.kgc.kmall.service;

import com.kgc.kmall.bean.PmsBaseAttrInfo;
import com.kgc.kmall.bean.PmsBaseAttrValue;

import java.util.List;
import java.util.Set;

/**
 * @author shkstart
 * @create 2020-12-16 16:12
 */
public interface AttrService {
    List<PmsBaseAttrInfo> select(Long catalog3Id);
    Integer add(PmsBaseAttrInfo attrInfo);
    List<PmsBaseAttrValue> getAttrValueList(Long attrId);
    List<PmsBaseAttrInfo> selectAttrInfoValueListByValueId(Set<Long> valueIds);
}
