package com.ksidelta.libruch.modules.user

import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.extensions.kotlin.applyEvent
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.spring.stereotype.Aggregate
import java.io.Serializable
import java.util.*

@Aggregate
class UserAggregate() {
    @AggregateIdentifier
    lateinit var id: ProviderTypeAndUserId

    @CommandHandler
    constructor(createUser: CreateUser) : this() {
        createUser.run { applyEvent(UserCreated(id, username, assignedGlobalId)) }
    }

    @EventSourcingHandler
    fun handle(userCreated: UserCreated) {
        id = userCreated.id
    }
}

data class CreateUser(
    val id: ProviderTypeAndUserId,
    val username: String,
    val assignedGlobalId: UUID = UUID.randomUUID()
)

data class UserCreated(
    val id: ProviderTypeAndUserId,
    val username: String,
    val assignedGlobalId: UUID
)

data class ProviderTypeAndUserId(
    var type: String,
    var userId: String,
) : Serializable


