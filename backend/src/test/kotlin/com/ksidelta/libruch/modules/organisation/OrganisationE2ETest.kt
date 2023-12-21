package com.ksidelta.libruch.modules.organisation

import com.ksidelta.libruch.BaseTest
import com.ksidelta.libruch.utils.eventuallyConfigured
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.test.StepVerifier
import java.time.Duration


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class OrganisationE2ETest : BaseTest() {
    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Qualifier("defaultUserId")
    lateinit var defaultUserId: String

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    fun whenOrganisationCreatedThenSucceeds() {
        createOrganisation("Hackespace Pomorze").expectStatus().isOk
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    fun whenOrganisationCreatedThenCanBeListed() = runBlocking {
        createOrganisation("Hackespace Pomorze").expectStatus().isOk
        createOrganisation("Hackespace NiePomorze").expectStatus().isOk

        eventuallyConfigured {
            listUserOrganisations().body?.organisations?.size shouldBe 2
        }
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    fun whenOrganisationCreatedThenNewListStreamed(): Unit = runBlocking {
        val stepVerifier = webTestClient
            .get()
            .uri("/api/organisation/stream")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
            .returnResult(UserOrganisationViewModel::class.java)
            .responseBody
            .let {
                StepVerifier.create(it)
            }

        createOrganisation("Psiaki").expectStatus().isOk
        eventuallyConfigured {
            listUserOrganisations().body?.organisations?.size shouldBe 1
        }

        val organisations = listUserOrganisations()
        stepVerifier
            .expectNext(UserOrganisationViewModel(emptyList()))
            .expectNext(
                UserOrganisationViewModel(
                        organisations.body?.organisations!!
                )
            )
            .verifyTimeout(Duration.ofMillis(100))
    }


    fun createOrganisation(name: String) =
        webTestClient
            .post()
            .uri("/api/organisation")
            .bodyValue(CreateOrganisationDTO(name))
            .exchange()

    fun listUserOrganisations() =
        testRestTemplate.exchange(
            RequestEntity.get("/api/organisation/mine")
                .build(),
            UserOrganisationViewModel::class.java
        )
}