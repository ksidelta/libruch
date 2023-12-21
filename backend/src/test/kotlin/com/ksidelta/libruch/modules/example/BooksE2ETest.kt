package com.ksidelta.libruch.modules.example

import com.ksidelta.libruch.BaseTest
import com.ksidelta.libruch.utils.assertBadRequest
import com.ksidelta.libruch.utils.assertBodyThat
import com.ksidelta.libruch.utils.assertOK
import com.ksidelta.libruch.utils.eventuallyConfigured
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.annotation.DirtiesContext
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BooksE2ETest : BaseTest() {
    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    val bookIsbn = "978-0134494166"

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    fun whenBookAddedThenSucceeds() {
        createBook(bookIsbn).assertOK()
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    fun givenBookBorrowedWhenBorrowedAgainThenErrorIs() {
        val book = createBook(bookIsbn)
        borrowBook(book.body!!.id).assertOK()

        borrowBook(book.body!!.id).assertBadRequest()
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    suspend fun given2BooksWhenListedThenBothPresent() = runBlocking {
        val book1 = createBook(bookIsbn).body!!.id
        val book2 = createBook(bookIsbn).body!!.id

        eventuallyConfigured {
            listBooks().assertBodyThat(
                equalTo(
                    BookAvailabilityListDTO(
                        listOf(
                            BookAvailabilityDTO(book1, bookIsbn, BookState.AVAILABLE),
                            BookAvailabilityDTO(book2, bookIsbn, BookState.AVAILABLE),
                        )
                    )
                )
            )
        }
    }

    fun createBook(bookIsbn: String) =
        testRestTemplate.postForEntity("/api/books", CreateBookDTO(bookIsbn), CreatedBookDTO::class.java)

    fun borrowBook(bookId: UUID) =
        testRestTemplate.postForEntity("/api/books/borrow", BorrowBookDTO(bookId), String::class.java)

    fun listBooks() =
        testRestTemplate.getForEntity("/api/books", BookAvailabilityListDTO::class.java)

}