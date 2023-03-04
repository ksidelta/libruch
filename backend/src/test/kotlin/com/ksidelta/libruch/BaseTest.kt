package com.ksidelta.libruch

import com.ksidelta.libruch.infra.user.MockUserProvider
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.context.event.ContextStoppedEvent
import org.springframework.context.event.EventListener
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer

open class BaseTest {
    companion object {
        val postgres = PostgreSQLContainer("postgres:15.2")
            .withPassword("postgres")
            .withUsername("postgres")
            .withDatabaseName("postgres")


        // Shit is idempotent so at least the server will be running
        @BeforeAll
        @JvmStatic
        fun runPostgres() {
            postgres.start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun realDataSourceURL(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { "jdbc:postgresql://localhost:${postgres.firstMappedPort}/postgres" }
        }
    }

    // Shit is idempotent so at least the server will be running
    @BeforeEach
    fun runIfSomethingHasThisAnnotationThatClearsContextEveryTime() {
        postgres.start()
    }

    // Normally TestContainers will just stop shit before Spring can take a dump or whatever.
    @EventListener
    fun stopItAfterTheSpringNotBeforeFFS(evt: ContextStoppedEvent) {
        postgres.stop()
    }

    @Primary
    @Bean
    fun authentication() = MockUserProvider()
}
