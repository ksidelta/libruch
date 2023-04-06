package com.ksidelta.libruch.modules.organisation

import com.ksidelta.libruch.modules.user.UserCreated
import org.axonframework.eventhandling.EventHandler
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id


@Service
class UsersForOrganisationsEventHandler(val repository: UsersForOrganisationRepository) {
    @EventHandler(payloadType = UserCreated::class)
    fun whenUserIsCreatedThenModelIsUpdated(created: UserCreated) {
        created.run {
            repository.save(
                UsersForOrganisationModel(
                    assignedGlobalId,
                    username
                )
            )
        }
    }
}

@Repository
interface UsersForOrganisationRepository : CrudRepository<UsersForOrganisationModel, UUID>

@Entity
data class UsersForOrganisationModel(
    @Id var userId: UUID,
    var userName: String
)