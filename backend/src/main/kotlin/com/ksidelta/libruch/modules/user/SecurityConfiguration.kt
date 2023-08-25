package com.ksidelta.libruch.modules.user

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.security.Principal
import java.util.*


/*
* Link for initiating authentication: "/auth/login/oauth2/{registrationId}"}
* RedirectURI: /auth/login/oauth2/callback/{registrationId}.
*/

@Configuration
@EnableWebFluxSecurity
class HelloWebfluxSecurityConfig {
    @Bean
    fun springSecurityFilterChain(
        http: ServerHttpSecurity,
        authorizationRequestResolver: ServerOAuth2AuthorizationRequestResolver
    ): SecurityWebFilterChain? {
        return http
            .csrf { it.disable() }
            .authorizeExchange { exchanges: AuthorizeExchangeSpec ->
                exchanges
                    .pathMatchers("/auth/login").authenticated()
                    .anyExchange().permitAll()
            }
            .oauth2Login {
                it.authorizationRequestResolver(authorizationRequestResolver)
                it.authenticationMatcher(PathPatternParserServerWebExchangeMatcher("/auth/login/oauth2/callback/{registrationId}"))
            }
            .exceptionHandling { it.authenticationEntryPoint(RedirectServerAuthenticationEntryPoint("/auth/login/oauth2/google")) }
            .build()
    }

    @Bean
    fun authorizationRequestResolver(clientRegistrationRepository: ReactiveClientRegistrationRepository): DefaultServerOAuth2AuthorizationRequestResolver {
        val authorizationRequestMatcher: ServerWebExchangeMatcher = PathPatternParserServerWebExchangeMatcher(
            "/auth/login/oauth2/{registrationId}"
        )

        return DefaultServerOAuth2AuthorizationRequestResolver(
            clientRegistrationRepository, authorizationRequestMatcher
        )
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