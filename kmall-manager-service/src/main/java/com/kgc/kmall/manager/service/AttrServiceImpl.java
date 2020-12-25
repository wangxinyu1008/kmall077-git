package com.kgc.kmall.manager.service;


import com.kgc.kmall.bean.PmsBaseAttrInfo;
import com.kgc.kmall.bean.PmsBaseAttrInfoExample;
import com.kgc.kmall.bean.PmsBaseAttrValue;
import com.kgc.kmall.bean.PmsBaseAttrValueExample;
import com.kgc.kmall.manager.mapper.PmsBaseAttrInfoMapper;
import com.kgc.kmall.manager.mapper.PmsBaseAttrValueMapper;
import com.kgc.kmall.service.AttrService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author shkstart
 * @create 2020-12-16 16:13
 */
@Service
@Component
public class AttrServiceImpl implements AttrService{
    @Resource
    PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;
    @Resource
    PmsBaseAttrValueMapper pmsBaseAttrValueMapper;

    @Override
    public List<PmsBaseAttrInfo> select(Long catalog3Id) {
        PmsBaseAttrInfoExample pmsBaseAttrInfoExample=new PmsBaseAttrInfoExample();
        PmsBaseAttrInfoExample.Criteria criteria = pmsBaseAttrInfoExample.createCriteria();
        criteria.andCatalog3IdEqualTo(catalog3Id);
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = pmsBaseAttrInfoMapper.selectByExample(pmsBaseAttrInfoExample);
        for (PmsBaseAttrInfo pmsBaseAttrInfo : pmsBaseAttrInfos) {
            PmsBaseAttrValueExample example1=new PmsBaseAttrValueExample();
            PmsBaseAttrValueExample.Criteria criteria1 = example1.createCriteria();
            criteria1.andAttrIdEqualTo(pmsBaseAttrInfo.getId());
            List<PmsBaseAttrValue> pmsBaseAttrValues = pmsBaseAttrValueMapper.selectByExample(example1);
            pmsBaseAttrInfo.setAttrValueList(pmsBaseAttrValues);
        }
        return pmsBaseAttrInfos;
    }

    @Override
    public Integer add(PmsBaseAttrInfo attrInfo) {
        int i=0;
        if(attrInfo.getId()==null) {
            i = pmsBaseAttrInfoMapper.insert(attrInfo);
        }else{
            i=pmsBaseAttrInfoMapper.updateByPrimaryKeySelective(attrInfo);
        }
        PmsBaseAttrValueExample pmsBaseAttrValueExample=new PmsBaseAttrValueExample();
        PmsBaseAttrValueExample.Criteria criteria = pmsBaseAttrValueExample.createCriteria();
        criteria.andAttrIdEqualTo(attrInfo.getId());
        i = pmsBaseAttrValueMapper.deleteByExample(pmsBaseAttrValueExample);
        if (attrInfo.getAttrValueList().size()>0){
            i=pmsBaseAttrValueMapper.insertBatch(attrInfo.getId(),attrInfo.getAttrValueList());
        }
        return i;
    }

    @Override
    public List<PmsBaseAttrValue> getAttrValueList(Long attrId) {
        PmsBaseAttrValueExample pmsBaseAttrValueExample=new PmsBaseAttrValueExample();
        PmsBaseAttrValueExample.Criteria criteria = pmsBaseAttrValueExample.createCriteria();
        criteria.andAttrIdEqualTo(attrId);
        List<PmsBaseAttrValue> pmsBaseAttrValues = pmsBaseAttrValueMapper.selectByExample(pmsBaseAttrValueExample);
        return pmsBaseAttrValues;
    }
}
