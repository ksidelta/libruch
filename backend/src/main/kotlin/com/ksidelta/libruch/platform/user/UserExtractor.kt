package com.ksidelta.libruch.platform.user

import com.ksidelta.libruch.modules.user.UserIdKey
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.security.Principal

interface UserExtractor {
    fun extract(principal: Principal): UserDetails
}

@Service
class GoogleUserExtractor : UserExtractor {
    override fun extract(principal: Principal): UserDetails =
        when (principal) {
            null -> throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
            is OAuth2AuthenticationToken -> {
                val sub = (principal.principal.attributes["sub"] as String)
                val username = (principal.principal.attributes["username"] ?: "Unnamed") as String
                UserDetails(UserIdKey("GOOGLE", sub), username)
            }

            else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Authorization is unknown")
        }
}

class UserDetails(val userIdKey: UserIdKey, val username: String)