package com.cts.distribution.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain chain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/manifests/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "/manifests/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/manifests/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/manifests/**").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(new HeaderAuthFilter(),
                    UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}