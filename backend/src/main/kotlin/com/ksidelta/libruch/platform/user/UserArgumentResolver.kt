package com.ksidelta.libruch.platform.user

import com.ksidelta.libruch.modules.kernel.Party
import com.ksidelta.libruch.modules.user.UserService
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
class UserArgumentResolver(val userService: UserService) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.parameterType.isAssignableFrom(Party.User::class.java)


    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> =
        exchange.getPrincipal<Principal?>()
            .publishOn(Schedulers.boundedElastic())
            .map {
                userService.findUser(it)
            }.switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.UNAUTHORIZED)))
                as Mono<Any>
}
