package com.ksidelta.libruch.modules.borrowing;

import com.ksidelta.libruch.infra.user.UserProvider
import com.ksidelta.libruch.infra.user.withParty
import kotlinx.coroutines.future.await
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.queryhandling.QueryGateway
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class BorrowingController (
    val userProvider: UserProvider,
    val commandGateway: CommandGateway
) {

    @PostMapping(path = ["/borrow"])
    suspend fun borrowCopy(@RequestBody body: BorrowCopyDTO) {
        userProvider.withParty { party ->
                commandGateway.send<Any>(BorrowCopy(body.id, party)).await()
        }
    }

    @PostMapping(path = ["/return"])
    suspend fun returnCopy(@RequestBody body: ReturnCopyDTO) {
        userProvider.withParty {  party ->
                commandGateway.send<Any>(ReturnCopy(body.id, party)).await()
        }
    }
    @ExceptionHandler(CopyAlreadyBorrowed::class, CopyAlreadyAvailable::class, OnlyBorrowerMayReturnCopy::class, OnlyBorrowedCopyMayByReturned::class)
    fun domainExceptionHandler(ex: Exception): Nothing =
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ex.message)
}

data class BorrowCopyDTO(val id: UUID)
data class ReturnCopyDTO(val id: UUID)