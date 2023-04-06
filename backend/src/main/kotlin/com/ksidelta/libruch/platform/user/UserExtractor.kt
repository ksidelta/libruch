package com.ksidelta.libruch.platform.user

import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.web.server.ResponseStatusException
import java.security.Principal

fun Principal?.userIdFromGoogle(): UserDetails =
    when (this) {
        null -> throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        is OAuth2AuthenticationToken -> {
            val sub = (this.principal.attributes["sub"] as String)
            val username = (this.principal.attributes["username"] ?: "Unnamed") as String
            UserDetails(UserIdKey("GOOGLE", sub), username)
        }

        else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Authorization is unknown")
    }

data class UserDetails(val userIdKey: UserIdKey, val username: String)