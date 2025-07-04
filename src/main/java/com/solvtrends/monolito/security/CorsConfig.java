package com.solvtrends.monolito.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

  public void addCorsMappings(CorsRegistry registry) {
    registry
            .addMapping("/**")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH","OPTIONS")
            .allowedHeaders("*")
            .allowedOrigins("*");
  }
}
