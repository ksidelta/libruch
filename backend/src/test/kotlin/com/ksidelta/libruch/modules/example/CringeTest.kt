package com.ksidelta.libruch.modules.example

import com.github.grantwest.eventually.EventuallyLambdaMatcher
import com.github.grantwest.eventually.EventuallyLambdaMatcher.eventuallyEval
import com.ksidelta.libruch.BaseTest
import com.ksidelta.libruch.modules.kernel.Party
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*
import java.util.function.Supplier

@SpringBootTest
class CringeTest : BaseTest() {
    @Autowired
    lateinit var commandGateway: CommandGateway

    @Autowired
    lateinit var queryGateway: QueryGateway

    @Test
    fun givenReturnedBookWhenQueriedForAllBooksThenBookIsProperlyDefined() {
        val party = Party(UUID.randomUUID())
        val borrowingParty = Party(UUID.randomUUID())
        val bookIsbn = "978-0134494166"

        val aggregateId = commandGateway.send<UUID>(RegisterNewBook(bookIsbn, party)).get()
        commandGateway.send<Any>(BorrowBook(aggregateId, borrowingParty)).get()
        commandGateway.send<Any>(ReturnBook(aggregateId, borrowingParty)).get()

        val ret: Supplier<List<BookAvailabilityModel>> =
            Supplier {
                queryGateway.query(
                    QueryAllBooks(),
                    ResponseTypes.multipleInstancesOf(BookAvailabilityModel::class.java)
                ).get()
            }


        assertThat(
            ret, eventuallyEval(
                equalTo(
                    listOf(
                        BookAvailabilityModel(
                            status = BookState.AVAILABLE,
                            id = aggregateId,
                            isbn = bookIsbn
                        )
                    )
                )
            )
        )
    }


    @Test
    fun givenBorrowedBookWhenDifferentPartyReturnsThenFails() {
        val party = Party(UUID.randomUUID())
        val borrowingParty = Party(UUID.randomUUID())
        val maliciousParty = Party(UUID.randomUUID())
        val bookIsbn = "978-0134494166"

        val aggregateId = commandGateway.send<UUID>(RegisterNewBook(bookIsbn, party)).get()
        commandGateway.send<Any>(BorrowBook(aggregateId, borrowingParty)).get()

        assertThrows<OnlyRenterMayReturnBook> {
            kotlin.runCatching { commandGateway.send<Any>(ReturnBook(aggregateId, maliciousParty)).get() }
                .onFailure { throw it.cause!! }
        }
    }
}