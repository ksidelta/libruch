package com.ksidelta.libruch.modules.user

import com.ksidelta.libruch.modules.kernel.Party
import com.ksidelta.libruch.modules.organisation.UserToOrganisationsModelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service
import java.security.Principal
import java.util.*
import javax.transaction.Transactional

@Service
class AuthenticationService(
    val userService: UserService,
    val userToOrganisationsModelRepository: UserToOrganisationsModelRepository,
) {
    final val USER_PARTY = "USER_PARTY"

    fun findUser(principal: Principal): Party.User {
        val userUUID = userService.logUser(principal)

        val userOrganisations =
            userToOrganisationsModelRepository.findAllByIdUserId(userUUID!!)
                .map { Party.Organisation(it.id.associatedGroup) }
                .toSet()

        return Party.User(userUUID!!, userOrganisations)
    }
}


suspend fun <R> AuthenticationService.withUser(principal: Principal, func: suspend (Party.User) -> R): R =
    withContext(Dispatchers.IO) {
        this@withUser.findUser(principal)
            .let { Party.User(it.id) }
            .let { func(it) }
    }