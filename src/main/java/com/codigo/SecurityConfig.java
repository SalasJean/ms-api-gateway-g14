package com.codigo;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity){
        return httpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange ->
                        exchange
                                .pathMatchers("/apis/codigo/api/authentication/v1/**").permitAll()
                                .pathMatchers("/apis/codigo/api/reniec/**").permitAll()
                                .anyExchange().authenticated()
                )
                .build();
    }
}
