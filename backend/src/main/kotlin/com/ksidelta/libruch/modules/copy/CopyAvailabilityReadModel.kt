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
class CopyEventProcessor(val copyReadModelRepository: CopyReadModelRepository) {
    @EventHandler
    fun handle(event: NewCopyRegistered, metadata: MetaData) =
        copyReadModelRepository.save(
            CopyAvailabilityModel(
                id = event.copyId,
                isbn = event.copy.isbn,
                title = event.copy.title,
                owner = event.owner.partyId,
                organisation = event.organisation.partyId,
            )
        )
}

@Repository
interface CopyReadModelRepository : CrudRepository<CopyAvailabilityModel, UUID> {
    fun findAllByOwner(owner: Party): Iterable<CopyAvailabilityModel>
    fun findAllByOwnerIn(owner: Collection<Party>): Iterable<CopyAvailabilityModel>
    fun findByIsbn(isbn: String): Optional<CopyAvailabilityModel>
    fun findAllByOrganisationAndTitleContains(organisation: UUID, titleFragment: String): Iterable<CopyAvailabilityModel>
}

@Entity(name = "copy_availability_model")
data class CopyAvailabilityModel(
    @Id
    val id: UUID,
    val isbn: String,
    val title: String,
    val owner: UUID,
    val organisation: UUID,
)


