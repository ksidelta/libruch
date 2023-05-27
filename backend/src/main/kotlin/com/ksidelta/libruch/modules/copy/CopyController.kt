package com.ksidelta.libruch.modules.copy

import com.ksidelta.libruch.modules.kernel.Party
import kotlinx.coroutines.future.await
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping(path = ["/api/copy"])
class CopyController(
    val bookSearchService: BookSearchService,
    val commandGateway: CommandGateway,
    val queryGateway: QueryGateway
) {

    @PostMapping
    suspend fun create(@RequestBody body: CreateCopyDTO, user: Party.User) =
        body.run {
            val copy = bookSearchService.get(isbn = body.isbn) ?: throw TODO()

            val aggregateId = commandGateway.send<UUID>(
                RegisterNewCopy(
                    copy = copy,
                    owner = Party.User(user.id),
                    organisation = Party.Organisation(organisationId)
                )
            ).await()
            CreatedCopyDTO(aggregateId)
        }

    //TODO: change QueryByOwners to QueryByOrganisations
    @GetMapping("/organisation")
    suspend fun listAllByOrganisations(user: Party.User): CopyAvailabilityListDTO =
        user.organisations.let { organisations ->
            queryGateway.query(
                QueryByOwners(owners = organisations),
                ResponseTypes.multipleInstancesOf(CopyAvailabilityModel::class.java)
            ).await()
                .map { it.run { CopyAvailabilityDTO(id = id, isbn = isbn, title = title) } }
                .let { CopyAvailabilityListDTO(it) }
        }

    @PostMapping("/by-organisation")
    suspend fun listAllByOrganisationMatching(@RequestBody body: GetCopyDTO, user: Party.User): CopyAvailabilityListDTO =
        body.run {
            queryGateway.query(
                QueryByOrganisationAndTitleFragment(organisationId, titleFragment),
                ResponseTypes.multipleInstancesOf(CopyAvailabilityModel::class.java)
            ).await()
                .map { it.run { CopyAvailabilityDTO(id = id, isbn = isbn, title = title) } }
                .let { CopyAvailabilityListDTO(it) }
        }
}

data class CreateCopyDTO(val isbn: String, val organisationId: UUID);
data class GetCopyDTO(val organisationId: UUID, val titleFragment: String);
data class CreatedCopyDTO(val id: UUID);

data class CopyAvailabilityListDTO(
    val copies: List<CopyAvailabilityDTO>
)

data class CopyAvailabilityDTO(
    val id: UUID,
    val title: String,
    var isbn: String,
)
