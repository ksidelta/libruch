package com.ksidelta.libruch.modules.organisation

import org.axonframework.eventhandling.DomainEventMessage
import org.axonframework.eventhandling.EventHandler
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service
import java.io.Serializable
import java.util.*
import javax.persistence.Embeddable
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Service
class UserToOrganisationsEventProcessor(val repository: UserToOrganisationsModelRepository) {

    @EventHandler
    fun handle(event: DomainEventMessage<MemberAdded>) {
        repository.save(
            UserToOrganisationsModel(
                UserAndOrganisation(event.payload.user.id, UUID.fromString(event.aggregateIdentifier)),
                event.payload.organisationName
            )
        )
    }
}

interface UserToOrganisationsModelRepository : CrudRepository<UserToOrganisationsModel, UserAndOrganisation>

@Entity
class UserToOrganisationsModel(
    @EmbeddedId
    var id: UserAndOrganisation,
    var organisationName: String,
)

@Embeddable
data class UserAndOrganisation(
    var user: UUID,
    var associatedGroup: UUID
) : Serializable