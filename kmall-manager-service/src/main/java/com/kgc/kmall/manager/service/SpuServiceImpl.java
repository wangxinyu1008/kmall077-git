package com.kgc.kmall.manager.service;

import com.kgc.kmall.bean.*;
import com.kgc.kmall.manager.mapper.*;
import com.kgc.kmall.service.SpuService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author shkstart
 * @create 2020-12-17 14:20
 */
@Service
@Component
public class SpuServiceImpl implements SpuService {
    @Resource
    PmsProductInfoMapper pmsProductInfoMapper;
    @Resource
    PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;
    @Resource
    PmsProductImageMapper pmsProductImageMapper;
    @Resource
    PmsProductSaleAttrMapper pmsProductSaleAttrMapper;
    @Resource
    PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;

    @Override
    public List<PmsProductInfo> spuList(Long catalog3Id) {
        PmsProductInfoExample pmsProductInfoExample=new PmsProductInfoExample();
        PmsProductInfoExample.Criteria criteria = pmsProductInfoExample.createCriteria();
        criteria.andCatalog3IdEqualTo(catalog3Id);
        List<PmsProductInfo> pmsProductInfos = pmsProductInfoMapper.selectByExample(pmsProductInfoExample);
        return pmsProductInfos;
    }

    @Override
    public List<PmsBaseSaleAttr> baseSaleAttrList() {
        List<PmsBaseSaleAttr> pmsBaseSaleAttrs = pmsBaseSaleAttrMapper.selectByExample(null);
        return pmsBaseSaleAttrs;
    }

    @Override
    public Integer saveSpuInfo(PmsProductInfo pmsProductInfo) {
        try {
            pmsProductInfoMapper.insert(pmsProductInfo);
            if(pmsProductInfo.getSpuImageList().size()>0&&pmsProductInfo.getSpuImageList()!=null) {
                pmsProductImageMapper.insertImages(pmsProductInfo.getId(),pmsProductInfo.getSpuImageList());
            }
            List<PmsProductSaleAttr> saleAttrList=pmsProductInfo.getSpuSaleAttrList();
            if(saleAttrList.size()>0&&saleAttrList!=null){
                for (PmsProductSaleAttr pmsProductSaleAttr : saleAttrList) {
                    pmsProductSaleAttr.setProductId(pmsProductInfo.getId());
                    int insert = pmsProductSaleAttrMapper.insert(pmsProductSaleAttr);
                    List<PmsProductSaleAttrValue> pmsProductSaleAttrValues=pmsProductSaleAttr.getSpuSaleAttrValueList();
                        if(insert>0) {
                            pmsProductSaleAttrValueMapper.insertSaleAttr(pmsProductInfo.getId(), pmsProductSaleAttrValues);
                        }
                }
            }
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }
    @Override
    public List<PmsProductSaleAttr> spuSaleAttrList(Long spuId) {
        PmsProductSaleAttrExample example=new PmsProductSaleAttrExample();
        PmsProductSaleAttrExample.Criteria criteria = example.createCriteria();
        criteria.andProductIdEqualTo(spuId);
        List<PmsProductSaleAttr> pmsProductSaleAttrList = pmsProductSaleAttrMapper.selectByExample(example);
        for (PmsProductSaleAttr pmsProductSaleAttr : pmsProductSaleAttrList) {
            PmsProductSaleAttrValueExample example1=new PmsProductSaleAttrValueExample();
            PmsProductSaleAttrValueExample.Criteria criteria1 = example1.createCriteria();
            criteria1.andSaleAttrIdEqualTo(pmsProductSaleAttr.getSaleAttrId());
            criteria1.andProductIdEqualTo(spuId);

            List<PmsProductSaleAttrValue> pmsProductSaleAttrValueList = pmsProductSaleAttrValueMapper.selectByExample(example1);
            pmsProductSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValueList);
        }
        return pmsProductSaleAttrList;
    }
    @Override
    public List<PmsProductImage> spuImageList(Long spuId) {
        PmsProductImageExample example=new PmsProductImageExample();
        PmsProductImageExample.Criteria criteria = example.createCriteria();
        criteria.andProductIdEqualTo(spuId);
        List<PmsProductImage> pmsProductImageList = pmsProductImageMapper.selectByExample(example);
        return pmsProductImageList;
    }

}
