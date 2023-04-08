package com.ksidelta.libruch.modules.organisation

import com.ksidelta.libruch.modules.kernel.Party
import com.ksidelta.libruch.modules.user.UserModelRepository
import org.axonframework.commandhandling.gateway.CommandGateway

open class OrganisationService(
    val commandGateway: CommandGateway,
    val userModelRepository: UserModelRepository,
    val userToOrganisationsModelRepository: UserToOrganisationsModelRepository
) {
    fun create(name: String, creator: Party.User) =
        commandGateway.send<Unit>(CreateOrganisation(name, creator))


    fun addMember() {}
}