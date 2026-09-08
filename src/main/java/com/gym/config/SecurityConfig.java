package com.gym.config;

import com.gym.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .authorizeHttpRequests(auth -> auth
                // Allow static resources, auth endpoints & H2 console
                .requestMatchers("/", "/login.html", "/css/**", "/js/**", "/images/**", "/favicon.ico", "/index.html").permitAll()
                .requestMatchers("/admin/**", "/member/**", "/trainer/**").permitAll() // These will load index.html pages which fetch REST APIs (REST API endpoints are secured below)
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                // REST API role-based authorization
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/chat/**").hasAnyRole("ADMIN", "MEMBER", "TRAINER")
                .requestMatchers("/api/member/**").hasAnyRole("ADMIN", "MEMBER")
                .requestMatchers("/api/trainers/**").hasAnyRole("ADMIN", "TRAINER", "MEMBER")
                .requestMatchers("/api/trainer/**").hasAnyRole("ADMIN", "TRAINER")
                .requestMatchers("/api/workout-plans/**").hasAnyRole("ADMIN", "TRAINER")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
