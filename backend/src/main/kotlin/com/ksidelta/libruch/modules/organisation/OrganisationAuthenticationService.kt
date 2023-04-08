package com.ksidelta.libruch.modules.organisation

import com.ksidelta.libruch.modules.kernel.Party
import com.ksidelta.libruch.modules.user.*
import com.ksidelta.libruch.platform.user.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.EventBus
import org.axonframework.messaging.MetaData
import org.springframework.stereotype.Service
import java.security.Principal
import java.util.*
import javax.transaction.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
class OrganisationAuthenticationService(
    val commandGateway: CommandGateway,
    val userExtractor: UserExtractor,
    val userModelRepository: UserModelRepository,
    val userToOrganisationsModelRepository: UserToOrganisationsModelRepository,
    val eventBus: EventBus
) {
    fun findUser(principal: Principal): Party.User {
        val userDetails = userExtractor.extract(principal)
        var userUUID = findUserByUserDetails(userDetails)

        if (userUUID == null) {
            runBlocking {
                eventBus.awaitingEvent(
                    UserReadModelUpdated::class
                ) { correlationId ->
                    userUUID = createUser(userDetails, correlationId)
                }
            }
        }

        val userOrganisations = findUserOrganisations(userUUID!!)

        return Party.User(userUUID!!, userOrganisations)
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun findUserByUserDetails(userDetails: UserDetails) =
        userDetails.userIdKey.let { userId ->
            userModelRepository
                .findById(UserIdKey(userId.type, userId.userId))
                .map { it.uuid }
                .getOrNull()
        }

    private suspend fun createUser(userDetails: UserDetails, correlationId: String): UUID {
        val userUUID = UUID.randomUUID()

        commandGateway.send<Unit>(
            CreateUser(
                ProviderTypeAndUserId(userDetails.userIdKey.type, userDetails.userIdKey.userId),
                userDetails.username,
                assignedGlobalId = userUUID
            ),
            MetaData(mutableMapOf(Pair("traceId", correlationId)))
        ).await()

        return userUUID
    }

    private fun findUserOrganisations(userID: UUID) =
        userToOrganisationsModelRepository.findAllByIdUserId(userID)
            .map { it.id.associatedGroup }
            .map { Party.Organisation(it) }
            .toSet()
}