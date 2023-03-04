package com.ksidelta.libruch.modules.product

import com.ksidelta.libruch.infra.user.UserProvider
import kotlinx.coroutines.future.await
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.security.Principal
import java.util.*

@RestController
@RequestMapping(path = ["/api/product"])
class ProductController(
    val userProvider: UserProvider,
    val commandGateway: CommandGateway,
    val queryGateway: QueryGateway
) {

    @PostMapping
    suspend fun create(principal: Principal, @RequestBody body: CreateProductDTO) =
        body.run {
            val user = userProvider.getUser(principal)
            val aggregateId = commandGateway.send<UUID>(RegisterNewProduct(isbn, title, author)).await()
            CreatedProductDTO(aggregateId)
        }

    @GetMapping
    suspend fun listAll(): ProductAvailabilityListDTO =
        queryGateway.query(QueryAllProducts(), ResponseTypes.multipleInstancesOf(ProductAvailabilityModel::class.java))
            .await()
            .map { it.run { ProductAvailabilityDTO(id = id, isbn = isbn, title = title, author = author) } }
            .let { ProductAvailabilityListDTO(it) }

    @ExceptionHandler(ProductAlreadyExist::class)
    fun domainExceptionHandler(ex: Exception): Nothing =
        throw ResponseStatusException(HttpStatus.BAD_REQUEST, ex.message)

}

data class CreateProductDTO(val isbn: String, val title: String, val author: String);
data class CreatedProductDTO(val id: UUID);


data class ProductAvailabilityListDTO(
    val products: List<ProductAvailabilityDTO>
)

data class ProductAvailabilityDTO(
    val id: UUID,
    val isbn: String,
    val title: String,
    val author: String,
)
