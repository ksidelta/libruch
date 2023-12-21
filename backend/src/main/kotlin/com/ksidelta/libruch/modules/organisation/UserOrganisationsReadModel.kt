package com.ksidelta.libruch.modules.organisation

import com.ksidelta.libruch.modules.kernel.Party
import org.axonframework.eventhandling.DomainEventMessage
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.gateway.EventGateway
import org.axonframework.queryhandling.QueryHandler
import org.axonframework.queryhandling.QueryUpdateEmitter
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
    val eventGateway: EventGateway,
    val queryUpdateEmitter: QueryUpdateEmitter
) {

    @EventHandler(payloadType = MemberAdded::class)
    fun handle(event: DomainEventMessage<MemberAdded>) {
        val userId = event.payload.user.id;
        val update =
            UserToOrganisationsModel(
                UserAndOrganisation(event.payload.user.id, UUID.fromString(event.aggregateIdentifier)),
                event.payload.organisationName
            )

        repository.save(
            update
        )


        queryUpdateEmitter.emit(QueryUserOrganisations::class.java, { query -> query.user.id == userId }, update)
    }

    @QueryHandler
    fun handleListUserOrganisations(listUserOrganisations: QueryUserOrganisations): List<UserOrganisationsView> =
        repository.findAllByIdAssociatedGroupIn(listUserOrganisations.user.organisations.map { it.id }.toList())
            .map { UserOrganisationsView(it.id.associatedGroup, it.organisationName) }

}

interface UserToOrganisationsModelRepository : CrudRepository<UserToOrganisationsModel, UserAndOrganisation> {
    fun findAllByIdAssociatedGroupIn(list: List<UUID>): List<UserToOrganisationsModel>
    fun findAllByIdUserId(userId: UUID): List<UserToOrganisationsModel>
}

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

data class QueryUserOrganisations(
    val user: Party.User
)

data class UserOrganisationsView(
    val organisationId: UUID,
    val organisationName: String
)


class UserToOrganisationsModelViewUpdated()