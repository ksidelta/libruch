package com.ksidelta.libruch.modules.example

import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Service

@Service
class QueryHandler(val bookReadModelRepository: BookReadModelRepository) {
    @QueryHandler
    fun query(queryAllBooks: QueryAllBooks) =
        bookReadModelRepository.findAll().toList()
}

class QueryAllBooks() {}
