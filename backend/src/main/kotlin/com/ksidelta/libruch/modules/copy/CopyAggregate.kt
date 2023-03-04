package com.ksidelta.libruch.modules.copy


import com.ksidelta.libruch.modules.kernel.Party
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.modelling.command.TargetAggregateIdentifier
import org.axonframework.spring.stereotype.Aggregate
import java.util.*

@Aggregate
class CopyAggregate() {

    @AggregateIdentifier
    lateinit var copyId: UUID

    @CommandHandler
    constructor(command: RegisterNewCopy) : this() {
        apply(command.run {
            NewCopyRegistered(
                copyId = UUID.randomUUID(),
                isbn = isbn,
                owner = owner,
            )
        })
    }

    @EventSourcingHandler
    fun on(evt: NewCopyRegistered) {
        this.copyId = evt.copyId
    }
}


data class RegisterNewCopy(val isbn: String, val owner: Party)


data class NewCopyRegistered(val copyId: UUID, val isbn: String, val owner: Party)
data class CopyBorrowed(val borrower: Party)
class CopyReturned()