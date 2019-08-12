package com.msj.douyin.config;

import com.msj.douyin.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/register")
                .excludePathPatterns("/login")
                .excludePathPatterns("/selectVideos")
                .excludePathPatterns("/images/**")
                .excludePathPatterns("/videos/**")
                .excludePathPatterns("/bgms/**                                   ")
                .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**");
    }

    @Override
    //可以在浏览器上通过访问127.0.0.1/images/xxx访问到D:/douyin/images/下的文件
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:D:\\douyin\\images\\");
        registry.addResourceHandler("/videos/**")
                .addResourceLocations("file:D:\\douyin\\videos\\");
        registry.addResourceHandler("/bgms/**")
                .addResourceLocations("file:D:\\douyin\\bgms\\");
    }
}
