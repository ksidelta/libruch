package com.ksidelta.libruch.modules.copy

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class BookApiConfig {

    @Bean("book-client")
    fun createBookApiClient(
        webBuilder: WebClient.Builder,
        @Value("\${api.search-book.base-url}") baseUrl: String,
    ): WebClient = webBuilder
        .baseUrl(baseUrl)
        .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .build()
}