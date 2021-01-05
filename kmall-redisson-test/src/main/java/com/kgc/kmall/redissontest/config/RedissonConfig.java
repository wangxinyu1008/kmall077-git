package com.kgc.kmall.redissontest.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author shkstart
 * @create 2021-01-04 15:26
 */
@Configuration
public class RedissonConfig {
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private String port;

//    @Bean
//    public RedissonClient redissonClient(){
//        Config config = new Config();
//        config.useSingleServer().setAddress("redis://"+host+":"+port);
//        RedissonClient redisson = Redisson.create(config);
//        return redisson;
//    }
}
