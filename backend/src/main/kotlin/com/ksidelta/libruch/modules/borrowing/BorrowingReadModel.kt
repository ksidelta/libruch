package com.ksidelta.libruch.modules.borrowing;

import com.ksidelta.libruch.modules.example.BookState
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Service
class BorrowingEventProcessor(
    val borrowingReadModelRepository: BorrowingReadModelRepository,
    val borrowingCopyReadModelRepository: BorrowingCopyReadModelRepository
) {
    @EventHandler
    fun handle(event: RegisteredNewBorrowing) {

        borrowingReadModelRepository.save(
            BorrowingOfferModel(
                id = event.uuid,
                isbn = event.isbn
            )
        )

        borrowingCopyReadModelRepository.save(BorrowingCopyModel(
            id = event.uuid,
            isbn = event.isbn,
            status = BookState.BORROWED,
            userId = event.userId

        ))
    }

    @QueryHandler
    fun queryBorrowedBooks(event: QueryBorrowedBooks): List<BorrowingCopyModel> =
        borrowingCopyReadModelRepository.findByUserId(event.userUUID)
}

data class QueryBorrowedBooks(val userUUID: UUID)

@Repository
interface BorrowingReadModelRepository : CrudRepository<BorrowingOfferModel, UUID> {
    fun findByIsbn(isbn: String): BorrowingOfferModel?
}

@Repository
interface BorrowingCopyReadModelRepository : CrudRepository<BorrowingCopyModel, UUID> {
    fun findByUserId(userUUID: UUID): List<BorrowingCopyModel>
}

@Entity(name = "borrowing_offer_model")
data class BorrowingOfferModel(
    @Id
    val id: UUID,
    val isbn: String
)

@Entity(name = "borrowed_copy_model")
data class BorrowingCopyModel(
    @Id
    val id: UUID,
    val isbn: String,
    val status: BookState,
    val userId: UUID
)
