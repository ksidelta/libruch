package com.ksidelta.libruch.modules.example

import com.ksidelta.libruch.BaseTest
import com.ksidelta.libruch.utils.assertBadRequest
import com.ksidelta.libruch.utils.assertBodyThat
import com.ksidelta.libruch.utils.assertOK
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
    fun whenBookAddedThenSucceeds() {
        createBook(bookIsbn).assertOK()
    }

    @Test
    fun givenBookBorrowedWhenBorrowedAgainThenErrorIs() {
        val book = createBook(bookIsbn)
        borrowBook(book.body!!.id).assertOK()

        borrowBook(book.body!!.id).assertBadRequest()
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    fun given2BooksWhenListedThenBothPresent() {
        val book1 = createBook(bookIsbn).body!!.id
        val book2 = createBook(bookIsbn).body!!.id

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

    fun createBook(bookIsbn: String) =
        testRestTemplate.postForEntity("/api/books", CreateBookDTO(bookIsbn), CreatedBookDTO::class.java)

    fun borrowBook(bookId: UUID) =
        testRestTemplate.postForEntity("/api/books/borrow", BorrowBookDTO(bookId), String::class.java)

    fun listBooks() =
        testRestTemplate.getForEntity("/api/books", BookAvailabilityListDTO::class.java)

}