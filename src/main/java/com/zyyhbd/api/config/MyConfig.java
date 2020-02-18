package com.zyyhbd.api.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.zyyhbd.api.component.MyLocalResolver;
import com.zyyhbd.api.interceptor.LoginInterceptor;

@Configuration
public class MyConfig implements WebMvcConfigurer {

	@Override
    public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/success").setViewName("success");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/callback");
    }

    @Bean
    public LocaleResolver localeResolver(){
        return new MyLocalResolver();
    }

}