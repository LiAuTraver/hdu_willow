package com.hdu.hdufpga.config;

import com.hdu.hdufpga.interceptor.VisitCountInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
//    @Resource
//    private VisitCountInterceptor visitCountInterceptor;
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(visitCountInterceptor).addPathPatterns("/**");
//    }

    @Value("${cors.allowed-origins}")
    private String[] allowedOrigins;


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("POST", "GET", "OPTIONS", "DELETE", "PUT")
                .allowedHeaders("x-requested-with", "satoken", "Content-Type", "Authorization", "token")
                .allowCredentials(true);
    }
}
