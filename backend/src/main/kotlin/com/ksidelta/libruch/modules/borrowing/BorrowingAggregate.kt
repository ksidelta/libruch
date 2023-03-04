package com.ksidelta.libruch.modules.borrowing

import com.ksidelta.libruch.modules.kernel.Party
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.extensions.kotlin.applyEvent
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.TargetAggregateIdentifier
import org.axonframework.spring.stereotype.Aggregate
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * After you have successfully added Product
 * in the ProductAggregate, then an event should be sent, which will create a borrowing offer
 * which effectively, makes the book available for exchange
 */
@Aggregate
class BorrowingAggregate() {

    @AggregateIdentifier
    lateinit var borrowing: UUID

    var copyState = CopyState.AVAILABLE
    var borrower: Party? = null
    var deadline: Instant? = null

    @CommandHandler
    constructor(command: RegisterNewBorrowing): this() {
        applyEvent(RegisteredNewBorrowing(command.isbn, UUID.randomUUID()))
    }

    @CommandHandler
    fun borrowCopy(borrowCopy: BorrowCopy) =
        if (copyState == CopyState.AVAILABLE)
            applyEvent(borrowCopy.run { CopyBorrowed(borrower) })
        else
            throw CopyAlreadyBorrowed()

    @CommandHandler
    fun returnCopy(returnCopy: ReturnCopy) =
        when {
            copyState == CopyState.AVAILABLE -> throw CopyAlreadyAvailable()
            copyState != CopyState.BORROWED -> throw OnlyBorrowedCopyMayByReturned()
            borrower != returnCopy.borrower -> throw OnlyBorrowerMayReturnCopy()
            else -> applyEvent(CopyReturned())
        }

    @EventSourcingHandler
    fun on(evt: CopyBorrowed) {
        this.copyState = CopyState.BORROWED
        this.borrower = evt.borrower
        this.deadline = Instant.now().plus(30, ChronoUnit.DAYS)
    }

    @EventSourcingHandler
    fun on(evt: CopyReturned) {
        this.copyState = CopyState.AVAILABLE
        this.borrower = null
    }

}

data class FindBorrowings (val isbn: String)

data class RegisterNewBorrowing (val isbn: String)
data class RegisteredNewBorrowing (val isbn: String, val uuid: UUID)

data class BorrowCopy(@TargetAggregateIdentifier val copyId: UUID, val borrower: Party)
data class ReturnCopy(@TargetAggregateIdentifier val copyId: UUID, val borrower: Party)
data class CopyBorrowed(val borrower: Party)
class CopyReturned()
enum class CopyState {
    AVAILABLE,
    RESERVED,
    BORROWED,
}
class CopyAlreadyBorrowed() : Exception("Copy Already Borrowed")
class CopyAlreadyAvailable() : Exception("Copy Already Returned")
class OnlyBorrowerMayReturnCopy() : Exception("Copy Returned By Wrong Party")
class OnlyBorrowedCopyMayByReturned() : Exception("Copy Not Borrowed")
