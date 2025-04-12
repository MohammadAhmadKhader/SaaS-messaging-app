package com.example.multitenant.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.multitenant.common.resolvers.impl.HandlePageResolver;
import com.example.multitenant.common.resolvers.impl.HandleSizeResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new HandlePageResolver());
        resolvers.add(new HandleSizeResolver());
    }
}
