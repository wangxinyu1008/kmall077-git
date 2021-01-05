package com.kgc.kmall.manager.service;

import com.alibaba.fastjson.JSON;
import com.kgc.kmall.bean.*;
import com.kgc.kmall.manager.mapper.PmsSkuAttrValueMapper;
import com.kgc.kmall.manager.mapper.PmsSkuImageMapper;
import com.kgc.kmall.manager.mapper.PmsSkuInfoMapper;
import com.kgc.kmall.manager.mapper.PmsSkuSaleAttrValueMapper;
import com.kgc.kmall.serviceutil.utils.RedisUtil;
import com.kgc.kmall.service.SkuService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import org.redisson.api.RedissonClient;
import javax.annotation.Resource;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;

/**
 * @author shkstart
 * @create 2020-12-23 19:15
 */

@Service
@Component
public class SkuServiceImpl  implements SkuService{
    @Resource
    PmsSkuInfoMapper pmsSkuInfoMapper;
    @Resource
    PmsSkuImageMapper pmsSkuImageMapper;
    @Resource
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;
    @Resource
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;
    @Resource
    RedisUtil redisUtil;
    @Resource
    RedissonClient redissonClient;
    @Override
    public String saveSkuInfo(PmsSkuInfo skuInfo) {
        pmsSkuInfoMapper.insert(skuInfo);
        Long skuInfoId = skuInfo.getId();
        for (PmsSkuImage pmsSkuImage : skuInfo.getSkuImageList()) {
            pmsSkuImage.setSkuId(skuInfoId);
            pmsSkuImageMapper.insert(pmsSkuImage);
        }
        for (PmsSkuAttrValue pmsSkuAttrValue : skuInfo.getSkuAttrValueList()) {
            pmsSkuAttrValue.setSkuId(skuInfoId);
            pmsSkuAttrValueMapper.insert(pmsSkuAttrValue);
        }
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuInfo.getSkuSaleAttrValueList()) {
            pmsSkuSaleAttrValue.setSkuId(skuInfoId);
            pmsSkuSaleAttrValueMapper.insert(pmsSkuSaleAttrValue);
        }
        return "success";
    }

    @Override
    public PmsSkuInfo selectBySkuId(Long id) {
        Jedis jedis= redisUtil.getJedis();
        String key="sku:"+id+":info";
        String skuJson=jedis.get(key);
        PmsSkuInfo pmsSkuInfo=null;
        if(skuJson!=null){
            pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);
            System.out.println("走缓存");
        }else {
            //拿到分布式锁
            Lock lock = redissonClient.getLock("lock");// 声明锁
            lock.lock();//上锁
            try {
                pmsSkuInfo = pmsSkuInfoMapper.selectByPrimaryKey(id);
                if (pmsSkuInfo != null) {
                    //保存到redis
                    String json = JSON.toJSONString(pmsSkuInfo);
                    Random random = new Random();
                    int i = random.nextInt(10);
                    if(i<1){
                        i++;
                    }
                    jedis.setex(key, i * 60 * 1000, json);
                } else {
                    jedis.setex(key, 5 * 60 * 1000, "empty");
                }
                System.out.println("走数据库");
            }finally {
                jedis.close();
                lock.unlock();
            }

        }
        return pmsSkuInfo;
    }

    @Override
    public List<PmsSkuInfo> selectBySpuId(Long spuId) {
//        Jedis jedis=redisUtil.getJedis();
//        String key="sku:"+spuId+":info";
//        String skuJson=jedis.get(key);
//        if(skuJson!=null){
//            List<PmsSkuInfo> pmsSkuInfos = JSON.parseArray(skuJson, PmsSkuInfo.class);
//            System.out.println("进缓存");
//            jedis.close();
//            return pmsSkuInfos;
//        }
        return pmsSkuInfoMapper.selectBySpuId(spuId);
    }

    @Override
    public List<PmsSkuInfo> getAllSku() {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectByExample(null);
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            PmsSkuAttrValueExample pmsSkuAttrValueExample=new PmsSkuAttrValueExample();
            PmsSkuAttrValueExample.Criteria criteria = pmsSkuAttrValueExample.createCriteria();
            criteria.andSkuIdEqualTo(pmsSkuInfo.getId());
            List<PmsSkuAttrValue> pmsSkuAttrValues = pmsSkuAttrValueMapper.selectByExample(pmsSkuAttrValueExample);
            pmsSkuInfo.setSkuAttrValueList(pmsSkuAttrValues);
        }
        return pmsSkuInfos;
    }
}
