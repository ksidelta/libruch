package com.ksidelta.libruch.infra.user

import com.ksidelta.libruch.modules.kernel.Party
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.security.Principal
import java.util.*

interface UserProvider {
    fun getUser(principal: Principal): User
}

@Service
class OAuthUserProvider() : UserProvider {
    override fun getUser(principal: Principal): User {
        val realPrincipal: Any = principal

        return when (realPrincipal) {
            null -> throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
            is OAuth2AuthenticationToken -> {
                val sub = (realPrincipal.principal.attributes["sub"] as String).toInt()

                User(UUID.randomUUID())
            }

            else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Authorization is unknown")
//        }
        }
    }
}

data class User(val id: UUID);

suspend fun <R> UserProvider.withParty(principal: Principal, func: suspend (Party) -> R): R =
    this.getUser(principal)
        .let { Party.User(it.id) }
        .let { func(it) }

suspend fun <R> UserProvider.withUser(principal: Principal, func: suspend (Party.User) -> R): R =
    this.getUser(principal)
        .let { Party.User(it.id) }
        .let { func(it) }