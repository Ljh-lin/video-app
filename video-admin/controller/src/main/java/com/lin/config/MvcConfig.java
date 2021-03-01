package com.lin.config;

import com.lin.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**")
                                .excludePathPatterns("/static/**")
                                .excludePathPatterns("/mvc/**")
                                .excludePathPatterns("/user/login");

    }

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/mvc/**")
                .addResourceLocations("file:D:/IDEA/videos-dev-resource/admin-bgm/");
    }
}
