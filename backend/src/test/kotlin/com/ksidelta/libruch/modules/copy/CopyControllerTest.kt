package com.ksidelta.libruch.modules.copy

import com.ksidelta.libruch.BaseTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CopyControllerTest : BaseTest() {

    @Autowired
    private lateinit var testRestTemplate: TestRestTemplate

    companion object {
        private const val URI = "/api/copy"
    }

    @Test
    fun `should return UUID after create copy`() {
        val isbn = "9788383223445"
        val organisationId = UUID.randomUUID()


        val result = testRestTemplate.postForEntity(URI, CreateCopyDTO(isbn, organisationId), CreatedCopyDTO::class.java)
        println(result)


        assertNotNull(result.body)
    }

    @Test
    fun `should return list of books after given title fragment`() {
        val organisationId = UUID.randomUUID()
        val isbn1 = "9788383223445"     // Czysty kod
        val isbn2 = "9788328364622"     // Czysty kod w Pythonie
        val isbn3 = "9781617297571"     // Spring in Action, Sixth Edition

        testRestTemplate.postForEntity(URI, CreateCopyDTO(isbn1, organisationId), String::class.java)
        testRestTemplate.postForEntity(URI, CreateCopyDTO(isbn2, organisationId), String::class.java)
        testRestTemplate.postForEntity(URI, CreateCopyDTO(isbn3, organisationId), String::class.java)

        val fragment = "Czysty"
        val request = GetCopyDTO(organisationId =organisationId, titleFragment = fragment)


        val result = testRestTemplate.postForEntity(URI.plus("/by-organisation"), request, CopyAvailabilityListDTO::class.java)


        assertNotNull(result.body)
        val copies = result.body!!.copies
        assertEquals(2, copies.size)
        assertTrue(copies.all { it.title.contains(fragment) })
    }
}