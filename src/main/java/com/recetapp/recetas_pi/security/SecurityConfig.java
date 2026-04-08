package com.recetapp.recetas_pi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                ).permitAll()
                .requestMatchers("/api/usuarios/register", "/api/usuarios/login").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/alergias/me").authenticated()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/alergias", "/api/alergias/*").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/recetas", "/api/recetas/**").permitAll()
                // Public metric endpoint used in recipe cards/lists without session
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/favoritos/countByReceta/*").permitAll()
                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}