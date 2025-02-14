package com.ksidelta.libruch.utils

import com.ksidelta.libruch.modules.organisation.UserOrganisationViewModel
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.test.StepVerifier


fun <T>WebTestClient.verifierForEventStream(uri: String, elementsType: Class<T>) =
    this.get()
        .uri("/api/organisation/stream")
        .accept(MediaType.TEXT_EVENT_STREAM)
        .exchange()
        .expectStatus().isOk
        .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
        .returnResult(elementsType)
        .responseBody
        .let {
            StepVerifier.create(it)
        }
