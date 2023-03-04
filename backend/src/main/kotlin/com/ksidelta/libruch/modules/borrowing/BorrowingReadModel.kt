package com.ksidelta.libruch.modules.borrowing;

import org.axonframework.eventhandling.EventHandler
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Service
class EventProcessor(val borrowingReadModelRepository: BorrowingReadModelRepository) {
    @EventHandler
    fun handle(event: RegisteredNewBorrowing) =
        borrowingReadModelRepository.save(
            BorrowingOfferModel(
                id =  event.uuid,
                isbn = event.isbn
            )
        )
}

@Repository
interface BorrowingReadModelRepository : CrudRepository<BorrowingOfferModel, UUID> {
    fun findByIsbn(isbn: String): BorrowingOfferModel?
}

@Entity(name = "borrowing_offer_model")
data class BorrowingOfferModel(
    @Id
    val id: UUID,
    val isbn: String
)
