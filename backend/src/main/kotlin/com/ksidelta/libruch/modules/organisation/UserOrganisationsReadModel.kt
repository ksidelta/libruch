package com.ksidelta.libruch.modules.organisation

import org.axonframework.eventhandling.DomainEventMessage
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.gateway.EventGateway
import org.axonframework.messaging.unitofwork.CurrentUnitOfWork
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service
import java.io.Serializable
import java.util.*
import javax.persistence.Embeddable
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Service
class UserToOrganisationsEventProcessor(
    val repository: UserToOrganisationsModelRepository,
    val eventGateway: EventGateway
) {

    @EventHandler(payloadType = MemberAdded::class)
    fun handle(event: DomainEventMessage<MemberAdded>) {
        repository.save(
            UserToOrganisationsModel(
                UserAndOrganisation(event.payload.user.id, UUID.fromString(event.aggregateIdentifier)),
                event.payload.organisationName
            )
        )

        eventGateway.publish(UserToOrganisationsModelViewUpdated())
    }
}

interface UserToOrganisationsModelRepository : CrudRepository<UserToOrganisationsModel, UserAndOrganisation>

@Entity
class UserToOrganisationsModel(
    @EmbeddedId
    var id: UserAndOrganisation,
    var organisationName: String,
) {}

@Embeddable
data class UserAndOrganisation(
    var userId: UUID,
    var associatedGroup: UUID
) : Serializable

class UserToOrganisationsModelViewUpdated()