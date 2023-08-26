package com.ksidelta.libruch.modules.copy


import com.ksidelta.libruch.modules.kernel.Party
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.spring.stereotype.Aggregate
import java.util.*

@Aggregate
class CopyAggregate() {

    @AggregateIdentifier
    lateinit var copyId: UUID
    lateinit var organisation: Party.Organisation
    lateinit var copyStatus: CopyStatus
    lateinit var borrower: Party.User
    lateinit var name: String
    @CommandHandler
    constructor(command: RegisterNewCopy) : this() {
        apply(command.run {
            NewCopyRegistered(
                copyId = UUID.randomUUID(),
                isbn = command.isbn,
                owner = command.owner,
                organisation = command.organisation,
                name = command.name
            )
        })
    }

    @EventSourcingHandler
    fun on(evt: NewCopyRegistered) {
        this.copyId = evt.copyId
        this.copyStatus = CopyStatus.AVAILABLE
    }
}


data class RegisterNewCopy(val isbn: String,val owner: Party.User, val organisation: Party.Organisation, val name: String) {
}


data class NewCopyRegistered(val copyId: UUID, val isbn: String, val owner: Party.User, val organisation: Party.Organisation, val name: String)

enum class CopyStatus{
    AVAILABLE, BORROWED
}