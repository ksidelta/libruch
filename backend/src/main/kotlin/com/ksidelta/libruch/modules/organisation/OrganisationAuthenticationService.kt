package com.ksidelta.libruch.modules.organisation

import com.ksidelta.libruch.modules.kernel.Party
import com.ksidelta.libruch.modules.user.CreateUser
import com.ksidelta.libruch.modules.user.ProviderTypeAndUserId
import com.ksidelta.libruch.platform.user.UserDetails
import com.ksidelta.libruch.platform.user.UserIdKey
import com.ksidelta.libruch.platform.user.UserModelRepository
import com.ksidelta.libruch.platform.user.userIdFromGoogle
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service
import java.security.Principal
import java.util.*
import javax.transaction.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
class OrganisationAuthenticationService(
    val commandGateway: CommandGateway,
    val userModelRepository: UserModelRepository,
    val userToOrganisationsModelRepository: UserToOrganisationsModelRepository
) {
    @Transactional
    fun findUser(principal: Principal): Party.User {
        val userDetails = principal.userIdFromGoogle()
        var userUUID = findUserByUserDetails(userDetails)

        if (userUUID == null) {
            userUUID = createUser(userDetails)
        }

        val userOrganisations = findUserOrganisations(userUUID)

        return Party.User(userUUID, userOrganisations)
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun findUserByUserDetails(userDetails: UserDetails) =
        userDetails.userIdKey.let { userId ->
            userModelRepository
                .findById(UserIdKey(userId.type, userId.userId))
                .map { it.uuid }
                .getOrNull()
        }

    private fun createUser(userDetails: UserDetails): UUID {
        val userUUID = UUID.randomUUID()

        commandGateway.send<Unit>(
            CreateUser(
                ProviderTypeAndUserId(userDetails.userIdKey.type, userDetails.userIdKey.userId),
                userDetails.username,
                assignedGlobalId = userUUID
            )
        )

        return userUUID
    }

    private fun findUserOrganisations(userID: UUID) =
        userToOrganisationsModelRepository.findAllByIdUserId(userID)
            .map { it.id.associatedGroup }
            .map { Party.Organisation(it) }
            .toSet()
}