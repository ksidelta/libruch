package com.ksidelta.libruch.utils

import org.springframework.data.repository.CrudRepository
import java.util.function.UnaryOperator

fun <T : Any, ID : Any> CrudRepository<T, ID>.findAndApply(id: ID, application: UnaryOperator<T>) {
    this.findById(id)
        .map(application)
        .map { it.apply { this@findAndApply.save(it) } }
        .orElseThrow { IllegalStateException("Can't find read model entity to apply") }
}