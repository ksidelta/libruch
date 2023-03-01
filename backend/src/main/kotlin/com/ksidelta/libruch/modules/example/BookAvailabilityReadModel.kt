package com.ksidelta.libruch.modules.example

import com.ksidelta.libruch.utils.findAndApply
import com.ksidelta.libruch.utils.handle
import org.axonframework.eventhandling.DomainEventMessage
import org.axonframework.eventhandling.EventHandler
import org.axonframework.messaging.MetaData
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id


@Service
class EventProcessor(val bookReadModelRepository: BookReadModelRepository) {
    @EventHandler
    fun handle(event: NewBookRegistered, metadata: MetaData) =
        bookReadModelRepository.save(
            BookAvailabilityModel(
                id = event.bookId,
                status = BookState.AVAILABLE,
                isbn = event.isbn
            )
        )

    @EventHandler(payloadType = BookBorrowed::class)
    fun handle(msg: DomainEventMessage<BookBorrowed>) =
        msg.handle { uuid, _ ->
            bookReadModelRepository.findAndApply(uuid) { it.apply { it.status = BookState.BORROWED } }
        }


    @EventHandler(payloadType = BookReturned::class)
    fun handle2(msg: DomainEventMessage<BookReturned>) =
        msg.handle { uuid, _ ->
            bookReadModelRepository.findAndApply(uuid) { it.apply { it.status = BookState.AVAILABLE } }
        }


}


@Repository
interface BookReadModelRepository : CrudRepository<BookAvailabilityModel, UUID> {
}

@Entity(name = "book_availability_model")
data class BookAvailabilityModel(
    @Id
    val id: UUID,
    var isbn: String,
    var status: BookState
)


