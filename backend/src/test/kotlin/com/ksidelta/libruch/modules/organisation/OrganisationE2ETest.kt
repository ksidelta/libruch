package com.ksidelta.libruch.modules.organisation

import com.ksidelta.libruch.BaseTest
import com.ksidelta.libruch.utils.assertOK
import com.ksidelta.libruch.utils.eventuallyConfigured
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.RequestEntity


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrganisationE2ETest : BaseTest() {
    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Test
    fun whenOrganisationCreatedThenSucceeds() {
        createOrganisation("Hackespace Pomorze").assertOK()
    }

    @Test
    fun whenOrganisationCreatedThenCanBeListed() = runBlocking {
        createOrganisation("Hackespace Pomorze").assertOK()

        eventuallyConfigured {
            listUserOrganisations().body?.organisations?.size shouldBe 2
        }
    }

    fun createOrganisation(name: String) =
        testRestTemplate.exchange(
            RequestEntity
                .post("/api/organisation")
                .body(CreateOrganisationDTO(name)),
            Unit::class.java
        )

    fun listUserOrganisations() =
        testRestTemplate.exchange(
            RequestEntity.get("/api/organisation/mine")
                .build(),
            UserOrganisationViewModel::class.java
        )
}