package com.ksidelta.libruch

import com.ksidelta.libruch.modules.kernel.Party
import com.ksidelta.libruch.modules.user.TestHeaderAuthenticationFilter
import com.ksidelta.libruch.modules.user.UserService
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.context.event.ContextStoppedEvent
import org.springframework.context.event.EventListener
import org.springframework.core.MethodParameter
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import org.testcontainers.containers.PostgreSQLContainer
import reactor.core.publisher.Mono
import java.security.Principal
import java.util.*

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
        @DynamicPropertySourcElo, jestem debilem i potrzebuję pomocy. Mam fedorę 38 zainstalowaną na btrfs z bootowaniem efi. Chciałem użyć timeshif do backupu systemu, ale ten wymaga żeby główny subvolume miał nazwę "@" . odpaliłem ubuntu live cd, zmieniłem nazwę subvolume na "@" i wyedytowałem pliki w grub oraz na partycji efi żeby zmienić odwołania do subcolume na nowe nazwy. Nie zadziałało. Odpaliłem grub rescue i kazałem mu naprawić gruba bez reinstalacji samego gruba - nic to nie dało. Więc zainstalowałem na drugiej partycji drugą fedorę z nadzieją że ta zainstaluje nowego gruba i wykryje starą fedorę ale tak się nie stało i do tego chyba nadpisała mi partycję efi.
e
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
}

@Configuration
class TestAuthenticationConfiguration() {

    @Bean
    @Qualifier("defaultUserId")
    fun defaultUserId() = UUID.randomUUID()

    @Bean
    fun restTemplateBuilder(@Qualifier("defaultUserId") defaultUserId: UUID): RestTemplateBuilder =
        RestTemplateBuilder().defaultHeader("X-USER-ID", defaultUserId.toString())

    @Bean
    @Order(value = Ordered.HIGHEST_PRECEDENCE)
    fun testFilterChain(http: ServerHttpSecurity) =
        http
            .csrf { it.disable() }
            .addFilterAt(TestHeaderAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
            .build()

}