package com.ksidelta.libruch.modules.user

import org.axonframework.eventhandling.EventHandler
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import java.io.Serializable
import java.util.*
import javax.persistence.Embeddable
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.IdClass

@Service
class UserEventHandler(val userModelRepository: UserModelRepository) {
    @EventHandler
    fun handleNewuser(userCreated: UserCreated) {
        userCreated.run {
            userModelRepository.save(
                UserModel(
                    UserIdKey(id.type, id.userId),
                    assignedGlobalId,
                    username
                )
            )
        }
    }
}

@Repository
interface UserModelRepository : CrudRepository<UserModel, UserIdKey> {
}

@Entity(name = "user_model")
data class UserModel(
    @EmbeddedId
    val userId: UserIdKey,
    val uuid: UUID,
    val username: String
)

@Embeddable
data class UserIdKey(
    var type: String,
    var userId: String,
) : Serializable


