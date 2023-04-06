package com.ksidelta.libruch.modules.organisation

import com.ksidelta.libruch.modules.kernel.Party
import com.ksidelta.libruch.modules.user.CreateUser
import com.ksidelta.libruch.modules.user.ProviderTypeAndUserId
import com.ksidelta.libruch.platform.user.UserDetails
import com.ksidelta.libruch.platform.user.UserIdKey
import com.ksidelta.libruch.platform.user.UserModelRepository
import com.ksidelta.libruch.platform.user.userIdFromGoogle
import org.axonframework.commandhandling.gateway.CommandGateway
import java.security.Principal
import java.util.*
import javax.transaction.Transactional
import kotlin.jvm.optionals.getOrNull

open class OrganisationService(
    val commandGateway: CommandGateway,
    val userModelRepository: UserModelRepository,
    val userToOrganisationsModelRepository: UserToOrganisationsModelRepository
) {
    fun create(name: String, creator: Party.User) =
        commandGateway.send<Unit>(CreateOrganisation(name, creator))


    fun addMember() {}




}