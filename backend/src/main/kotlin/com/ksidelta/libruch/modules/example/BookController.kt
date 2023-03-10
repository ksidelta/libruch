package com.ksidelta.libruch.modules.example

import com.ksidelta.libruch.modules.kernel.Party
import com.ksidelta.libruch.modules.user.UserService
import com.ksidelta.libruch.modules.user.withUser
import kotlinx.coroutines.future.await
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.security.Principal
import java.util.*

// TODO: Change this error handling as it makes me nuts!!!
// Possibly use flux and Complete your way out of here
@RestController
@RequestMapping(path = ["/api/books"])
class BookController(
    val userService: UserService,
    val commandGateway: CommandGateway,
    val queryGateway: QueryGateway
) {

    @PostMapping
    suspend fun create(principal: Principal, @RequestBody body: CreateBookDTO) =
        body.run {
            val user = userService.findUser(principal)
            val aggregateId = commandGateway.send<UUID>(RegisterNewBook(isbn, Party.User(user.id))).await()
            CreatedBookDTO(aggregateId)
        }

    @PostMapping(path = ["/borrow"])
    suspend fun borrowBook(principal: Principal, @RequestBody body: BorrowBookDTO) {
        userService.withUser(principal) { party ->
            commandGateway.send<Any>(BorrowBook(body.id, party)).await()
        }
    }

    @PostMapping(path = ["/return"])
    suspend fun returnBook(principal: Principal, @RequestBody body: ReturnBookDTO) {
        userService.withUser(principal) { party ->
            commandGateway.send<Any>(ReturnBook(body.id, party)).await()
        }
    }

    @GetMapping
    suspend fun listAll(): BookAvailabilityListDTO =
        queryGateway.query(QueryAllBooks(), ResponseTypes.multipleInstancesOf(BookAvailabilityModel::class.java))
            .await()
            .map { it.run { BookAvailabilityDTO(id = id, isbn = isbn, status = status) } }
            .let { BookAvailabilityListDTO(it) }

    @ExceptionHandler(BookAlreadyBorrowed::class, BookAlreadyAvailable::class, OnlyRenterMayReturnBook::class)
    fun domainExceptionHandler(ex: Exception): Nothing =
        throw ResponseStatusException(HttpStatus.BAD_REQUEST, ex.message)

}

data class CreateBookDTO(val isbn: String);
data class CreatedBookDTO(val id: UUID);

data class BorrowBookDTO(val id: UUID)
data class ReturnBookDTO(val id: UUID)

data class BookAvailabilityListDTO(
    val books: List<BookAvailabilityDTO>
)

data class BookAvailabilityDTO(
    val id: UUID,
    var isbn: String,
    var status: BookState
)
