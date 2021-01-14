package com.kgc.kmall.config;

import com.kgc.kmall.interceptors.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author shkstart
 * @create 2021-01-12 15:28
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Resource
    AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor).addPathPatterns("/**");
    }
}
