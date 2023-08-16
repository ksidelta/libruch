package com.ksidelta.libruch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.retry.annotation.EnableRetry

@SpringBootApplication
@EnableRetry
class LibruchApplication

fun main(args: Array<String>) {
	runApplication<LibruchApplication>(*args)
}
