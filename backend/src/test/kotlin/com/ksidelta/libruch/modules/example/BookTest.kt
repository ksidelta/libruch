package com.ksidelta.libruch.modules.example

import com.ksidelta.libruch.BaseTest
import com.ksidelta.libruch.modules.kernel.Party
import com.ksidelta.libruch.utils.eventuallyConfigured
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.timing.eventually
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class BookTest : BaseTest() {
    @Autowired
    lateinit var commandGateway: CommandGateway

    @Autowired
    lateinit var queryGateway: QueryGateway

    @Test
    fun givenReturnedBookWhenQueriedForAllBooksThenBookIsProperlyDefined() = runBlocking {
        val party = Party.User(UUID.randomUUID())
        val borrowingParty = Party.User(UUID.randomUUID())
        val bookIsbn = "978-0134494166"

        val aggregateId = commandGateway.send<UUID>(RegisterNewBook(bookIsbn, party)).await()
        commandGateway.send<Any>(BorrowBook(aggregateId, borrowingParty)).await()
        commandGateway.send<Any>(ReturnBook(aggregateId, borrowingParty)).await()

        val query = suspend {
            queryGateway.query(
                QueryAllBooks(),
                ResponseTypes.multipleInstancesOf(BookAvailabilityModel::class.java)
            ).await()
        }


        eventuallyConfigured {
            query() shouldBe
                    listOf(
                        BookAvailabilityModel(
                            status = BookState.AVAILABLE, id = aggregateId, isbn = bookIsbn
                        )
                    )
        }
    }


    @Test
    fun givenBorrowedBookWhenDifferentPartyReturnsThenFails(): Unit = runBlocking {
        val party = Party.User(UUID.randomUUID())
        val borrowingParty = Party.User(UUID.randomUUID())
        val maliciousParty = Party.User(UUID.randomUUID())
        val bookIsbn = "978-0134494166"

        val aggregateId = commandGateway.send<UUID>(RegisterNewBook(bookIsbn, party)).await()
        commandGateway.send<Any>(BorrowBook(aggregateId, borrowingParty)).await()

        shouldThrow<OnlyRenterMayReturnBook> {
            commandGateway.send<Any>(ReturnBook(aggregateId, maliciousParty)).await()
        }
    }
}