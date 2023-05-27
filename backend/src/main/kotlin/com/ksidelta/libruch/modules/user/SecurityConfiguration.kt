package com.ksidelta.libruch.modules.user

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver
import org.springframework.security.web.server.SecurityWebFilterChain


/*
* Link for initiating authentication: "/oauth2/authorization/google"}
* Callbacks: /login/oauth2/code/google.
*/

@Configuration
@EnableWebFluxSecurity
class HelloWebfluxSecurityConfig {
    @Bean
    fun springSecurityFilterChain(
        http: ServerHttpSecurity
    ): SecurityWebFilterChain? {
        http
            .csrf { it.disable() }
            //.exceptionHandling { it.authenticationEntryPoint(RedirectServerAuthenticationEntryPoint("/auth/login")) }
            .authorizeExchange { exchanges: AuthorizeExchangeSpec ->
                exchanges
                    .pathMatchers("/auth/login").authenticated()
                    .anyExchange().permitAll()
            }
            .oauth2Login()


        return http.build()
    }
}