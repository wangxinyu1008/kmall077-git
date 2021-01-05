package com.kgc.kmall.searchservice.config;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author shkstart
 * @create 2021-01-04 18:46
 */
@Configuration
public class MyConfig {
    @Bean
    public JestClient getJestClient(){
        JestClientFactory jestClientFactory=new JestClientFactory();
        jestClientFactory.setHttpClientConfig(new HttpClientConfig.Builder("http://192.168.134.140:9200")
        .multiThreaded(true)
        .build());
        return jestClientFactory.getObject();
    }
}
