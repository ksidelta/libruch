package com.ksidelta.libruch.modules.product

import org.axonframework.eventhandling.EventHandler
import org.axonframework.messaging.MetaData
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id


@Service
class EventProcessor(val productReadModelRepository: ProductReadModelRepository) {
    @EventHandler
    fun handle(event: NewProductRegistered, metadata: MetaData) =
        productReadModelRepository.save(
            ProductAvailabilityModel(
                id = event.bookId,
                isbn = event.isbn,
                title = event.title,
                author = event.author,
            )
        )
}


@Repository
interface ProductReadModelRepository : CrudRepository<ProductAvailabilityModel, UUID> {
    fun findByIsbn(isbn: String): ProductAvailabilityModel?
}

@Entity(name = "product_availability_model")
data class ProductAvailabilityModel(
    @Id
    val id: UUID,
    val isbn: String,
    val title: String,
    val author: String,
)


