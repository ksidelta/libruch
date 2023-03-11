package com.ksidelta.libruch.modules.organisation

import com.ksidelta.libruch.modules.kernel.Party
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.queryhandling.QueryGateway

class OrganisationService(val commandGateway: CommandGateway, val queryGateway: QueryGateway) {
    fun create(name: String, creator: Party.User) =
        commandGateway.send<Unit>(CreateOrganisation(name, creator))


    fun addMember() {}
}