package com.ksidelta.libruch.infra.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec
import org.springframework.security.web.server.SecurityWebFilterChain


@Configuration
@EnableWebFluxSecurity
class HelloWebfluxSecurityConfig {
//    @Bean
//    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
//        http
//            .authorizeExchange { exchanges: AuthorizeExchangeSpec ->
//                exchanges
//                    .anyExchange().authenticated()
//            }
//            .oauth2Client()
//            .httpBasic(withDefaults())
//            .formLogin(withDefaults())
//        return http.build()
//    }
}