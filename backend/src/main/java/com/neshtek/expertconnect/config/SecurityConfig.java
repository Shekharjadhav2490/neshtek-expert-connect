package com.neshtek.expertconnect.config;

import com.neshtek.expertconnect.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter){this.jwtAuthenticationFilter=jwtAuthenticationFilter;}

    @Bean
    PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/consultation-requests/*/accept", "/api/v1/consultation-requests/*/reject")
                    .hasAnyRole("EXPERT", "ADMIN")
                .requestMatchers("/api/v1/consultation-requests/experts/**").hasAnyRole("EXPERT", "ADMIN")
                .requestMatchers("/api/v1/consultation-requests/customers/**").hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers("/api/v1/consultation-requests").hasAnyRole("CUSTOMER", "ADMIN")
                .anyRequest().authenticated())
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
