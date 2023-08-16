package com.ksidelta.libruch.modules.user

import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.http.HttpStatus
import org.springframework.orm.jpa.JpaSystemException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.security.Principal
import java.util.*
import javax.persistence.PersistenceException
import javax.transaction.Transactional
import kotlin.jvm.optionals.getOrNull

interface UserService {
    fun logUser(principal: Principal): UUID
}

@Service
class UserServiceImpl(
    val userModelRepository: UserModelRepository,
    val commandGateway: CommandGateway
) : UserService {

    @OptIn(ExperimentalStdlibApi::class)
    @Retryable(
        value = [PersistenceException::class, JpaSystemException::class],
        maxAttempts = 5,
        backoff = Backoff(delay = 100, multiplier = 2.0)
    )
    @Transactional
    override fun logUser(principal: Principal): UUID {

        val userDetails = principal.userIdFromGoogle()
        val userId = userDetails.userIdKey

        var userUUID = userModelRepository
            .findById(UserIdKey(userId.type, userId.userId))
            .map { it.uuid }
            .getOrNull()

        if (userUUID == null) {
            userUUID = UUID.randomUUID()

            commandGateway.send<Unit>(
                CreateUser(
                    ProviderTypeAndUserId(userId.type, userId.userId),
                    userDetails.username
                )
            )
        }

        return userUUID!!
    }
}

fun Principal?.userIdFromGoogle(): UserDetails =
    when (this) {
        null -> throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        is OAuth2AuthenticationToken -> {
            val sub = (this.principal.attributes["sub"] as String)
            val username = (this.principal.attributes["username"] ?: "Unnamed") as String
            UserDetails(UserIdKey("GOOGLE", sub), username)
        }

        is SimpleAuthentication -> {
            UserDetails(
                UserIdKey("TEST", (this.principal as SimpleAuthentication.TestPrincipal).id.toString()),
                "ZIOMEK"
            )
        }

        else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Authorization is unknown")
    }

data class UserDetails(val userIdKey: UserIdKey, val username: String)
