package com.kgc.kmall.orderservice.service;

import com.kgc.kmall.service.OrderService;
import com.kgc.kmall.serviceutil.utils.RedisUtil;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author shkstart
 * @create 2021-01-15 17:00
 */
@Service
@Component
public class OrderServiceImpl implements OrderService{
    @Resource
    RedisUtil redisUtil;
    @Override
    public String genTradeCode(Long aLong) {
        Jedis jedis = redisUtil.getJedis();

        String tradeKey = "user:"+aLong+":tradeCode";

        String tradeCode = UUID.randomUUID().toString();

        jedis.setex(tradeKey,60*15,tradeCode);

        jedis.close();

        return tradeCode;
    }

    @Override
    public String checkTradeCode(Long aLong, String tradeCode) {
        Jedis jedis = redisUtil.getJedis();
        String tradeKey = "user:"+aLong+":tradeCode";
        String code=jedis.get(tradeKey);
        if(code!=null&&code.equals(tradeCode)){
            jedis.del(tradeKey);
            return "success";
        }else{
            return "fail";
        }

    }
}
