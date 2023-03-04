package com.ksidelta.libruch.modules.user

import com.ksidelta.libruch.modules.kernel.Party
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.security.Principal
import java.util.*
import javax.transaction.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
class UserService(val userModelRepository: UserModelRepository) {
    final val USER_PARTY = "USER_PARTY"

    @OptIn(ExperimentalStdlibApi::class)
    @Transactional
    fun findUser(principal: Principal): Party.User {
        val userId = principal.userIdFromGoogle()

        var userUUID = userModelRepository
            .findById(UserIdKey(userId.type, userId.userId))
            .map { it.uuid }
            .getOrNull()

        if (userUUID == null) {
            userUUID = UUID.randomUUID()
            userModelRepository.save(
                UserModel(
                    UserIdKey(userId.type, userId.userId),
                    userUUID
                )
            )
        }

        return Party.User(userUUID!!)
    }
}

fun Principal?.userIdFromGoogle(): UserIdKey =
    when (this) {
        null -> throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        is OAuth2AuthenticationToken -> {
            val sub = (this.principal.attributes["sub"] as String)
            UserIdKey("GOOGLE", sub)
        }

        else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Authorization is unknown")
    }

suspend fun <R> UserService.withUser(principal: Principal, func: suspend (Party.User) -> R): R =
    this.findUser(principal)
        .let { Party.User(it.id) }
        .let { func(it) }
