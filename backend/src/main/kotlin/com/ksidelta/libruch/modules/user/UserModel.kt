package com.ksidelta.libruch.modules.user

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.io.Serializable
import java.util.*
import javax.persistence.Embeddable
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.IdClass


@Repository
interface UserModelRepository : CrudRepository<UserModel, UserIdKey> {
}

@Entity(name = "user_model")
data class UserModel(
    @EmbeddedId
    val userId: UserIdKey,
    val uuid: UUID
)

@Embeddable
data class UserIdKey(
    var type: String,
    var userId: String,
) : Serializable


