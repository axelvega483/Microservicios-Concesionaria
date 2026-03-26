package com.Concesionaria.api_gateway.security;

import com.Concesionaria.api_gateway.security.filter.JwtTokenValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private final JwtTokenValidator jwtTokenValidator;

    public SecurityConfig(JwtTokenValidator jwtTokenValidator) {
        this.jwtTokenValidator = jwtTokenValidator;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/auth-service/auth/**").permitAll()

                        .pathMatchers("/customer-service/**").authenticated()
                        .pathMatchers("/catalog-service/**").authenticated()
                        .pathMatchers("/sales-service/**").authenticated()
                        .pathMatchers("/auth-service/user/**").authenticated()
                        .pathMatchers("/payments-service/**").authenticated()
                        .anyExchange().authenticated()
                )
                .addFilterBefore(jwtTokenValidator, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}