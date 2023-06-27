package com.example.todo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")//어떤 요청에 대해서 허용할지?
                .allowedOrigins("http://localhost:3000") //어떤 클라이언트를 허용할지
                .allowedMethods("*") //어떤 요청 방식을 허용할지
                .allowedHeaders("*") //어떤 요청 헤더를 허용할지
                .allowCredentials(true) //쿠키 전달 허용 여부
                .maxAge(3600); //캐싱시간 설정
    }

}
