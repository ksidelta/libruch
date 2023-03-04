package com.ksidelta.libruch.infra.user

import com.ksidelta.libruch.modules.kernel.Party
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*
import java.util.function.Function

interface UserProvider {
    fun getUser(): User
}


@Service
class MockUserProvider : UserProvider {
    val userUUID = UUID.randomUUID()
    override fun getUser(): User = User(userUUID)
}

class SpringAuthBasedUserProvider : UserProvider {
    override fun getUser(): User {
        val principal = SecurityContextHolder.getContext().authentication.principal

        return when (principal) {
            null -> throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
            is User -> principal
            else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Authorization is unknown")
        }
    }
}

data class User(val id: UUID);

suspend fun <R> UserProvider.withParty(func: suspend (Party) -> R): R =
    this.getUser()
        .let { Party(it.id) }
        .let { func(it) }

suspend fun <R> UserProvider.withUser(func: suspend (Party.User) -> R): R =
    this.getUser()
        .let { Party.User(it.id) }
        .let { func(it) }