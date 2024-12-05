package com.iopexdemo.itime_backend.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${api.base-path}")
    String basePath;

    @Override
    public void configurePathMatch(PathMatchConfigurer configure) {
        configure.addPathPrefix(basePath, c -> true);
    }
}

