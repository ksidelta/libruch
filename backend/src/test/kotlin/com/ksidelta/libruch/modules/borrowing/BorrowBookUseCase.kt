package com.ksidelta.libruch.modules.borrowing

import com.ksidelta.libruch.BaseTest
import com.ksidelta.libruch.MockUserArgumentResolver
import com.ksidelta.libruch.modules.example.BookState
import com.ksidelta.libruch.utils.eventuallyConfigured
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BorrowBookUseCase : BaseTest() {

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Autowired
    lateinit var gateway: CommandGateway

    @Autowired
    lateinit var queryGateway: QueryGateway

    @Autowired
    lateinit var mockUserArgumentResolver : MockUserArgumentResolver;

        final val isbnConstant = "1234"
    @Test
    fun test() = runBlocking {
        val newBorrowingCompletableFuture = gateway.send<UUID>(RegisterNewBorrowing(isbnConstant));
        val uuid = newBorrowingCompletableFuture.await()
        testRestTemplate.postForEntity("/api/borrow", BorrowCopyDTO(uuid), Void::class.java)
        Assertions.assertTrue(true)

        val userUUID = mockUserArgumentResolver.user.id
        val query = suspend {
            queryGateway.query(
                QueryBorrowedBooks(userUUID),
                ResponseTypes.multipleInstancesOf(BorrowingCopyModel::class.java)
            ).await()
        }

        eventuallyConfigured {
            query() shouldBe
                    listOf(
                        BorrowingCopyModel(
                            status = BookState.BORROWED,
                            id = uuid,
                            isbn = isbnConstant,
                            userId = userUUID
                        )
                    )
        }


    }
}