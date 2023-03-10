package com.ksidelta.libruch.modules.borrowing;

import com.ksidelta.libruch.modules.kernel.Party
import kotlinx.coroutines.future.await
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class BorrowingController(
    val commandGateway: CommandGateway
) {

    @PostMapping(path = ["/borrow"])
    suspend fun borrowCopy(user: Party.User, @RequestBody body: BorrowCopyDTO) {
        commandGateway.send<Any>(BorrowCopy(body.id, user)).await()
    }

    @PostMapping(path = ["/return"])
    suspend fun returnCopy(user: Party.User, @RequestBody body: ReturnCopyDTO) {
        commandGateway.send<Any>(ReturnCopy(body.id, user)).await()
    }

    @ExceptionHandler(
        CopyAlreadyBorrowed::class,
        CopyAlreadyAvailable::class,
        OnlyBorrowerMayReturnCopy::class,
        OnlyBorrowedCopyMayByReturned::class
    )
    fun domainExceptionHandler(ex: Exception): Nothing =
        throw ResponseStatusException(HttpStatus.BAD_REQUEST, ex.message)
}

data class BorrowCopyDTO(val id: UUID)
data class ReturnCopyDTO(val id: UUID)