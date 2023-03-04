package com.ksidelta.libruch.modules.product

import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Service

@Service
class ProductQueryHandler(val productReadModelRepository: ProductReadModelRepository) {
    @QueryHandler
    fun query(queryAllProducts: QueryAllProducts) =
        productReadModelRepository.findAll().toList()
}

class QueryAllProducts() {}
