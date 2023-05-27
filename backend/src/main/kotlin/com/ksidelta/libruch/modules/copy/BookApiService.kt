package com.ksidelta.libruch.modules.copy

import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Component
class BookApiService(
    @Qualifier("book-client") private val webClient: WebClient,
    @Value("\${api.search-book.key}") private val apiKey: String,
) {

    suspend fun search(isbn: String): BookInfo? = webClient.get()
        .uri {
            it.path("/volumes")
                .queryParam("q", "isbn:$isbn")
                .queryParam("key", apiKey)
                .build()
        }
        .retrieve()
        .bodyToMono(BookVolumes::class.java)
        .awaitSingle()
        .items
        .firstOrNull()
        ?.bookInfo

    private data class BookVolumes(
        @JsonProperty("items") val items: List<VolumeItem>
    )

    private data class VolumeItem(
        @JsonProperty("volumeInfo") val bookInfo: BookInfo,
    )

    data class BookInfo(
        @JsonProperty("title") val title: String,
        @JsonProperty("subtitle") val subtitle: String = "",
        @JsonProperty("authors") val authors: List<String> = emptyList(),
        @JsonProperty("publishedDate") val publishedDate: String = "",
        @JsonProperty("language") val language: String = "",
        @JsonProperty("industryIdentifiers") val industryIdentifiers: List<IndustryIdentifier>,
    )

    data class IndustryIdentifier(
        @JsonProperty("type") val type: String,
        @JsonProperty("identifier") val identifier: String
    )
}
