package com.ksidelta.libruch.modules.copy

import com.ksidelta.libruch.modules.kernel.Party
import org.axonframework.eventhandling.EventHandler
import org.axonframework.messaging.MetaData
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import kotlin.collections.Collection


@Service
class EventProcessor(val copyReadModelRepository: CopyReadModelRepository) {
    @EventHandler
    fun handle(event: NewCopyRegistered, metadata: MetaData) =
        copyReadModelRepository.save(
            CopyAvailabilityModel(
                id = event.copyId,
                isbn = event.isbn,
                owner = event.owner,
            )
        )
}

@Repository
interface CopyReadModelRepository : CrudRepository<CopyAvailabilityModel, UUID> {
    fun findAllByOwner(owner: Party): Iterable<CopyAvailabilityModel>
    fun findAllByOwnerIn(owner: Collection<Party>): Iterable<CopyAvailabilityModel>
}

@Entity(name = "copy_availability_model")
data class CopyAvailabilityModel(
    @Id
    val id: UUID,
    val isbn: String,
    val owner: Party,
)


