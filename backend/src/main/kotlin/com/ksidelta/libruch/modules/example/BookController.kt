package com.ksidelta.libruch.modules.example

import com.ksidelta.libruch.infra.user.UserProvider
import com.ksidelta.libruch.infra.user.withParty
import com.ksidelta.libruch.modules.kernel.Party
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*
import java.util.concurrent.ExecutionException

// TODO: Change this error handling as it makes me nuts!!!
// Possibly use flux and Complete your way out of here
@RestController
@RequestMapping(path = ["/api/books"])
class BookController(
    val userProvider: UserProvider,
    val commandGateway: CommandGateway,
    val queryGateway: QueryGateway
) {

    @PostMapping
    fun create(@RequestBody body: CreateBookDTO) =
        body.run {
            val user = userProvider.getUser()
            val aggregateId = commandGateway.send<UUID>(RegisterNewBook(isbn, Party(user.id))).get()
            CreatedBookDTO(aggregateId)
        }

    @PostMapping(path = ["/borrow"])
    fun borrowBook(@RequestBody body: BorrowBookDTO) {
        userProvider.withParty { party ->
            commandGateway.send<Any>(BorrowBook(body.id, party)).get()
        }
    }

    @PostMapping(path = ["/return"])
    fun returnBook(@RequestBody body: ReturnBookDTO) {
        userProvider.withParty { party ->
            commandGateway.send<Any>(ReturnBook(body.id, party)).get()
        }
    }

    @GetMapping
    fun listAll(): List<BookAvailabilityDTO> =
        queryGateway.query(QueryAllBooks(), ResponseTypes.multipleInstancesOf(BookAvailabilityModel::class.java))
            .get().map { it.run { BookAvailabilityDTO(id = id, isbn = isbn, status = status) } }

    @ExceptionHandler(ExecutionException::class)
    fun domainExceptionHandler(ex: ExecutionException): Nothing =
        when (ex.cause) {
            is BookAlreadyAvailable,
            is BookAlreadyBorrowed,
            is OnlyRenterMayReturnBook ->
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, ex.cause!!.message)
            else -> throw ex
        }
}

data class CreateBookDTO(val isbn: String);
data class CreatedBookDTO(val id: UUID);

data class BorrowBookDTO(val id: UUID)
data class ReturnBookDTO(val id: UUID)

data class BookAvailabilityDTO(
    val id: UUID,
    var isbn: String,
    var status: BookState
)
