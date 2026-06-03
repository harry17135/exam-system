package com.exam.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 公开访问
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/doc.html", "/webjars/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
                // 前端静态资源（SPA + 资源文件）
                .requestMatchers("/", "/index.html", "/assets/**", "/vite.svg", "/favicon.ico").permitAll()
                // 角色权限
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/teacher/**").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers("/api/student/**").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                .requestMatchers("/api/statistics/**").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers("/api/users/**", "/api/classes/**", "/api/questions/**",
                        "/api/papers/**", "/api/exam/**", "/api/arrangements/**").authenticated()
                .anyRequest().permitAll()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
