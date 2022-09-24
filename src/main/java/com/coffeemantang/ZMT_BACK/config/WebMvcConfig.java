package com.coffeemantang.ZMT_BACK.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Web Config
@Configuration // 스프링 빈으로 등록
public class WebMvcConfig implements WebMvcConfigurer {
    // 유지시간
    private final long MAX_AGE_SECS = 3600;

    // Cors 방지
    @Override
    public void addCorsMappings(CorsRegistry registry){
        //모든 경로에 대해
        registry.addMapping("/**")
                // Origin이 http:localhost:3000(front)에 대해
                .allowedOrigins("http://localhost:3000")
                // GET, POST, PUT, PATCH, DELETE, OPTIONS 메서드 허용
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true) // 인증에 관한 정보 허용
                .maxAge(MAX_AGE_SECS);
    }
}
