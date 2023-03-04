package com.ksidelta.libruch.modules.copy

import com.ksidelta.libruch.infra.user.UserProvider
import com.ksidelta.libruch.infra.user.withUser
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
    val userProvider: UserProvider,
    val commandGateway: CommandGateway,
    val queryGateway: QueryGateway
) {

    @PostMapping
    suspend fun create(@RequestBody body: CreateCopyDTO) =
        body.run {
            val user = userProvider.getUser()
            val aggregateId = commandGateway.send<UUID>(RegisterNewCopy(isbn, Party.User(user.id))).await()
            CreatedCopyDTO(aggregateId)
        }

    @GetMapping
    suspend fun listAllByOrganisations(): CopyAvailabilityListDTO =
        userProvider.withUser { it.organisations }.let { organisations ->
            queryGateway.query(QueryByOwners(owners = organisations), ResponseTypes.multipleInstancesOf(CopyAvailabilityModel::class.java)).await()
                .map { it.run { CopyAvailabilityDTO(id = id, isbn = isbn) } }
                .let { CopyAvailabilityListDTO(it) }
        }

}

data class CreateCopyDTO(val isbn: String);
data class CreatedCopyDTO(val id: UUID);

data class CopyAvailabilityListDTO(
    val copies: List<CopyAvailabilityDTO>
)

data class CopyAvailabilityDTO(
    val id: UUID,
    var isbn: String,
)
