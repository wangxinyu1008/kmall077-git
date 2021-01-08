package com.kgc.kmall.cartservice.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kgc.kmall.bean.OmsCartItem;
import com.kgc.kmall.bean.OmsCartItemExample;
import com.kgc.kmall.cartservice.mapper.OmsCartItemMapper;
import com.kgc.kmall.service.CartService;
import com.kgc.kmall.serviceutil.utils.RedisUtil;
import org.apache.dubbo.config.annotation.Service;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * @author shkstart
 * @create 2021-01-08 16:28
 */
@Component
@Service
public class CartServiceImpl implements CartService {
    @Resource
    OmsCartItemMapper omsCartItemMapper;
    @Resource
    RedisUtil redisUtil;
    @Resource
    RedissonClient redissonClient;

    @Override
    public OmsCartItem ifCartExistByUser(String memberId, long skuId) {
        OmsCartItemExample omsCartItemExample=new OmsCartItemExample();
        OmsCartItemExample.Criteria criteria = omsCartItemExample.createCriteria();
        criteria.andMemberIdEqualTo(Long.parseLong(memberId));
        criteria.andProductSkuIdEqualTo(skuId);
        List<OmsCartItem> omsCartItems = omsCartItemMapper.selectByExample(omsCartItemExample);
        if(omsCartItems!=null&&omsCartItems.size()>0){
            return omsCartItems.get(0);
        }else{
            return null;
        }
    }

    @Override
    public void addCart(OmsCartItem omsCartItem) {
        omsCartItemMapper.insertSelective(omsCartItem);
    }

    @Override
    public void updateCart(OmsCartItem omsCartItemFromDb) {
        omsCartItemMapper.updateByPrimaryKeySelective(omsCartItemFromDb);
    }

    @Override
    public void flushCartCache(String memberId) {
        OmsCartItemExample omsCartItemExample=new OmsCartItemExample();
        OmsCartItemExample.Criteria criteria = omsCartItemExample.createCriteria();
        criteria.andMemberIdEqualTo(Long.parseLong(memberId));
        List<OmsCartItem> omsCartItems = omsCartItemMapper.selectByExample(omsCartItemExample);
        Jedis jedis = redisUtil.getJedis();
        Map<String,String> map=new HashMap<>();
        for (OmsCartItem omsCartItem : omsCartItems) {
            map.put(omsCartItem.getProductSkuId().toString(), JSON.toJSONString(omsCartItem));
        }
        jedis.del("user:"+memberId+":cart");
        jedis.hmset("user:"+memberId+":cart",map);
        jedis.close();
    }

    @Override
    public List<OmsCartItem> cartList(String memberId) {
        Jedis jedis=null;
        List<OmsCartItem> omsCartItems=new ArrayList<>();
   //     Lock lock=null;
        try {
            jedis = redisUtil.getJedis();
            List<String> hvals = jedis.hvals("user:" + memberId + ":cart");
            if(hvals!=null){
                for (String hval : hvals) {
                    OmsCartItem  omsCartItem = JSON.parseObject(hval,OmsCartItem.class);
                    omsCartItems.add(omsCartItem);
                }
            }else{
                //拿到分布式锁
//                lock = redissonClient.getLock("omslock");// 声明锁
//                lock.lock();//上锁
                OmsCartItemExample omsCartItemExample=new OmsCartItemExample();
                omsCartItemExample.createCriteria().andMemberIdEqualTo(Long.parseLong(memberId));
                omsCartItems=omsCartItemMapper.selectByExample(omsCartItemExample);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
           jedis.close();
          // lock.unlock();
        }
        return omsCartItems;
    }
}
