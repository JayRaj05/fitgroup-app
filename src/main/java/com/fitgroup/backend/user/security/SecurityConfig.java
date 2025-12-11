package com.fitgroup.backend.user.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth

                        // ----------------------------------------
                        // Auth endpoints (no login required)
                        // ----------------------------------------
                        .requestMatchers("/api/auth/**").permitAll()

                        // ----------------------------------------
                        // Challenge-specific rules
                        // More specific paths FIRST
                        // ----------------------------------------
                        .requestMatchers("/api/challenges/*/finish").authenticated()
                        .requestMatchers("/api/badges/my").authenticated()
                        .requestMatchers("/api/challenges/{id}/leaderboard").authenticated()
                        .requestMatchers("/api/challenges/my").authenticated()
                        .requestMatchers("/api/challenges/{id}/join").authenticated()


                        // Public browsing allowed
                        .requestMatchers("/api/challenges/public").permitAll()

                        // All other challenge routes require login
                        .requestMatchers("/api/challenges/**").authenticated()

                        // ----------------------------------------
                        // Everything else requires auth
                        // ----------------------------------------
                        .anyRequest().authenticated()
                );

        // Add JWT filter
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}