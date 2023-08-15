package com.ksidelta.libruch.modules.user

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec
import org.springframework.security.core.AuthenticatedPrincipal
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.security.Principal
import java.util.*


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
        return http
            .csrf { it.disable() }
            .addFilterAt(TestHeaderAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
            .authorizeExchange { exchanges: AuthorizeExchangeSpec ->
                exchanges
                    .pathMatchers("/auth/login").authenticated()
                    .anyExchange().permitAll()
            }
            .oauth2Login()
            .and()
            .build()
    }


}


class TestHeaderAuthenticationFilter() : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val userId: String? = exchange.request.headers.getFirst("X-USER-ID")

        if (userId != null) {
            val uuid = UUID.fromString(userId)

            val securityContext = SecurityContextImpl()
            securityContext.authentication = SimpleAuthentication(uuid)

            return chain.filter(exchange)
                .subscriberContext(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
        }

        return chain.filter(exchange)
    }

}

class SimpleAuthentication(val uuid: UUID) : Authentication {
    override fun getName(): String {
        return "test"
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf()
    }

    override fun getCredentials(): Any {
        return Object()
    }

    override fun getDetails(): Any {
        return Object()
    }

    override fun getPrincipal(): Any {
        return TestPrincipal(uuid)
    }

    override fun isAuthenticated(): Boolean {
        return true;
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
    }

    class TestPrincipal(val id: UUID) : Principal {
        override fun getName(): String {
            return id.toString()
        }
    }
}