package com.ksidelta.libruch.modules.copy


import com.ksidelta.libruch.modules.kernel.Party
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.extensions.kotlin.applyEvent
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.spring.stereotype.Aggregate
import java.util.*

@Aggregate
class CopyAggregate() {

    @AggregateIdentifier
    lateinit var copyId: UUID

    @CommandHandler
    constructor(command: RegisterNewCopy) : this() {
        applyEvent(
            NewCopyRegistered(
                copyId = UUID.randomUUID(),
                copy = command.copy,
                owner = command.owner,
                organisation = command.organisation,
            )
        )
    }

    @EventSourcingHandler
    fun on(evt: NewCopyRegistered) {
        this.copyId = evt.copyId
    }
}


data class RegisterNewCopy(val copy: BookDetails, val owner: Party.User, val organisation: Party.Organisation)


data class NewCopyRegistered(
    val copyId: UUID,
    val copy: BookDetails,
    val owner: Party,
    val organisation: Party.Organisation
)