package com.ksidelta.libruch.modules.copy

import com.ksidelta.libruch.modules.kernel.Party
import com.ksidelta.libruch.modules.user.AuthenticationService
import com.ksidelta.libruch.modules.user.withUser
import kotlinx.coroutines.future.await
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.util.*

@RestController
@RequestMapping(path = ["/api/copy"])
class CopyController(
    val authenticationService: AuthenticationService,
    val commandGateway: CommandGateway,
    val queryGateway: QueryGateway
) {

    @PostMapping
    suspend fun create(@RequestBody body: CreateCopyDTO, principal: Principal) =
        body.run {
            val user = authenticationService.findUser(principal)
            val aggregateId = commandGateway.send<UUID>(
                RegisterNewCopy(
                    isbn,
                    user,
                    Party.Organisation(body.organisationID),
                    body.name
                )
            ).await()
            CreatedCopyDTO(aggregateId)
        }

    @GetMapping
    suspend fun listAllByOrganisations(principal: Principal): CopyAvailabilityListDTO =
        authenticationService.withUser(principal) { it.organisations }.let { organisations ->
            queryGateway.query(
                QueryByOwners(owners = organisations),
                ResponseTypes.multipleInstancesOf(CopyAvailabilityModel::class.java)
            ).await()
                .map { it.run { CopyAvailabilityDTO(id = id, isbn = isbn) } }
                .let { CopyAvailabilityListDTO(it) }
        }

    @GetMapping("/search")
    suspend fun listCopiesByName(@RequestBody body: searchDTO, principal: Principal) : SearchCopyListDTO =
        authenticationService.withUser(principal) {it.organisations}.let {
            queryGateway.query(
                QueryByNameAndOrganisations(
                    body.name,
                    authenticationService.findUser(principal),
                    authenticationService.findUser(principal).organisations),
                ResponseTypes.multipleInstancesOf(CopyAvailabilityModel::class.java)
            ).await()
                .map { it.run { CopyAvailabilityDTO(id = it.id, isbn = it.isbn) } }
                .let { SearchCopyListDTO(it) }
        }


}


class searchDTO (val name: String) {
}

data class SearchCopyListDTO (val l: List<CopyAvailabilityDTO>)

data class CreateCopyDTO(val isbn: String, val organisationID: UUID, val owner: Party.User, val name: String)
data class CreatedCopyDTO(val id: UUID)

data class CopyAvailabilityListDTO(
    val copies: List<CopyAvailabilityDTO>
)

data class CopyAvailabilityDTO(
    val id: UUID,
    var isbn: String
)
