package com.ksidelta.libruch.platform.organisation

import com.ksidelta.libruch.modules.kernel.Party
import com.ksidelta.libruch.modules.organisation.OrganisationAuthenticationService
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.security.Principal

@Component
class UserArgumentResolver(val authenticationService: OrganisationAuthenticationService) :
    HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.parameterType.isAssignableFrom(Party.User::class.java)


    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> {
        return exchange.getPrincipal<Principal>()
            .switchIfEmpty(Mono.just<Principal?>(EmptyPrincipal()))
            .publishOn(Schedulers.boundedElastic())
            .map {
                authenticationService.findUser(it)
            }
    }
}

class EmptyPrincipal : Principal {
    override fun getName(): String = "Anonymous"
}
