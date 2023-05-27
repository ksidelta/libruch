package com.ksidelta.libruch.modules.copy

import com.ksidelta.libruch.BaseTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CopyControllerTest : BaseTest() {

    @Autowired
    private lateinit var testRestTemplate: TestRestTemplate

    companion object {
        private const val URI = "/api/copy"
    }

}