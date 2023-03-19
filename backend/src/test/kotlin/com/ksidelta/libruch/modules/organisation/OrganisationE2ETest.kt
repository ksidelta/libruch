package com.ksidelta.libruch.modules.organisation

import com.ksidelta.libruch.BaseTest
import com.ksidelta.libruch.utils.assertOK
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrganisationE2ETest : BaseTest() {
    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Test
    fun whenOrganisationCreatedThenSucceeds() {
        createBook("Hackespace Pomorze").assertOK()
    }

    fun createBook(name: String) =
        testRestTemplate.postForEntity("/api/organisation", CreateOrganisationDTO(name), Unit::class.java)
}