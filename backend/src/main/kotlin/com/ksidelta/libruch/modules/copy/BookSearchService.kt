package com.ksidelta.libruch.modules.copy

import org.springframework.stereotype.Service


@Service
class BookSearchService(
    private val bookApiService: BookApiService,
) {

    private val cache: MutableMap<String, BookDetails> = mutableMapOf()

    suspend fun get(isbn: String): BookDetails? =
        cache.getOrElse(isbn) { search(isbn) }

    private suspend fun search(isbn: String): BookDetails? =
        bookApiService.search(isbn = isbn)?.let {
            BookDetails(
                isbn = it.industryIdentifiers.first { i -> i.type == "ISBN_13" }.identifier,
                title = it.title,
                subtitle = it.subtitle,
                authors = it.authors.toString(),
                publishedDate = it.publishedDate,
                language = it.language,
            )
        }
}

data class BookDetails(
    val isbn: String,
    val title: String,
    val subtitle: String,
    val authors: String,
    val publishedDate: String,
    val language: String,
)

