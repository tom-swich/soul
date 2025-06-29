package com.solvtrends.monolito.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.GET;

@Configuration
public class WebSecurityConfig {

    @Autowired
    private JWTFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                                .requestMatchers("/user/reset-password").permitAll()
                        .requestMatchers(GET, "/actuator/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(GET, "/swagger-ui.html")
                        .permitAll()
                        .requestMatchers(GET, "/swagger-ui/**")
                        .permitAll()
                        .requestMatchers(GET, "/v3/api-docs")
                        .permitAll()
                        .requestMatchers(GET, "/v3/api-docs/")
                        .permitAll()
                        .requestMatchers(GET, "/v3/api-docs/v1")
                        .permitAll()
                        .requestMatchers(GET, "/v3/api-docs/swagger-config")
                        .permitAll()
                        .requestMatchers(GET, "/configuration/ui")
                        .permitAll()
                        .requestMatchers(GET, "/swagger-resources")
                        .permitAll()
                        .requestMatchers(GET, "/configuration/security")
                        .permitAll()
                        .requestMatchers(GET, "/webjars/**")
                        .permitAll()
                        .requestMatchers(GET, "/favicon.ico")
                        .permitAll()
                        .requestMatchers("/error")
                        .permitAll()
                        .anyRequest().authenticated()
                ).sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
