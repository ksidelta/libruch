package com.ksidelta.libruch.modules.user

import com.ksidelta.libruch.modules.kernel.Party
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.gateway.EventGateway
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.security.Principal
import java.util.*
import javax.transaction.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
class UserService(val userModelRepository: UserModelRepository, val commandGateway: CommandGateway) {
    final val USER_PARTY = "USER_PARTY"

    @OptIn(ExperimentalStdlibApi::class)
    @Transactional
    fun findUser(principal: Principal): Party.User {

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

        return Party.User(userUUID!!)
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

        else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Authorization is unknown")
    }

data class UserDetails(val userIdKey: UserIdKey, val username: String)

suspend fun <R> UserService.withUser(principal: Principal, func: suspend (Party.User) -> R): R =
    withContext(Dispatchers.IO) {
        this@withUser.findUser(principal)
            .let { Party.User(it.id) }
            .let { func(it) }
    }

class NotYetProvisionedException : Exception("Your account is not yet provisioned, check later")