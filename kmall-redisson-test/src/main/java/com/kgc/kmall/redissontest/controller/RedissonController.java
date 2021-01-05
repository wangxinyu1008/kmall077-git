package com.kgc.kmall.redissontest.controller;


import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;

/**
 * @author shkstart
 * @create 2021-01-04 14:33
 */
@RestController
public class RedissonController {
//    RedisUtil redisUtil=new RedisUtil();
//    @Resource
//    redisson
//    @RequestMapping("/test")
//    public String testRedisson(){
//        redisUtil.initPool("192.168.134.140",6379,0);
//        Jedis jedis = redisUtil.getJedis();
//        try {
//            String v = jedis.get("k");
//            if (v==null) {
//                v = "1";
//            }
//            System.out.println("->" + v);
//            jedis.set("k", (Integer.parseInt(v) + 1) + "");
//        }finally {
//            jedis.close();
//        }
//        return "success";
//    }
}
