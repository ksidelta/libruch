package com.ksidelta.libruch.modules.organisation

import com.ksidelta.libruch.modules.kernel.Party
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withTimeout
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.EventBus
import org.axonframework.messaging.MetaData
import org.axonframework.messaging.responsetypes.ResponseType
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.toFlux
import java.time.Duration
import java.util.*
import kotlin.reflect.KClass

@RestController
@RequestMapping("/api/organisation")
class OrganisationController(
    val commandGateway: CommandGateway,
    val eventBus: EventBus,
    val queryGateway: QueryGateway
) {

    @PostMapping
    suspend fun create(user: Party.User, @RequestBody createOrganisationDTO: CreateOrganisationDTO) {
        eventBus.awaitingEvent(OrganisationCreated::class) { correlationId ->
            createOrganisationDTO.let {
                commandGateway.send<Unit>(
                    CreateOrganisation(it.name, user),
                    MetaData(mutableMapOf(Pair("traceId", correlationId)))
                )
            }.await()
        }
    }

    @GetMapping(path = ["/mine"])
    suspend fun listUserOrganisations(user: Party.User): UserOrganisationViewModel =
        queryGateway.query(
            QueryUserOrganisations(user),
            ResponseTypes.multipleInstancesOf(UserOrganisationsView::class.java)
        )
            .await()
            .let { UserOrganisationViewModel(it) }

    @GetMapping(path = ["/stream"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun testServerSentEvents(user: Party.User): Flux<UserOrganisationViewModel> =
        queryGateway.subscriptionQuery(
            QueryUserOrganisations(user),
            ResponseTypes.multipleInstancesOf(UserOrganisationsView::class.java),
            ResponseTypes.multipleInstancesOf(UserOrganisationsView::class.java)
        ).let {
            Flux.concat(
                it.initialResult().toFlux(),
                it.updates()
            ).map { UserOrganisationViewModel(it) }
        }
}

suspend fun EventBus.awaitingEvent(eventType: KClass<*>, block: suspend (correlationId: String) -> Unit) {
    withTimeout(10000) {
        val correlationId = UUID.randomUUID().toString()

        val flow =
            callbackFlow {
                val x = this
                val subscription = this@awaitingEvent.subscribe { events ->
                    events.forEach {
                        if (it.payload.javaClass == eventType.java && it.metaData["traceId"] == correlationId) {
                            x.trySend(Unit)
                            x.close()
                        } else {
                            println(it.payload)
                        }
                    }
                }

                block(correlationId)

                awaitClose {
                    subscription.close()
                }
            }

        flow.first()
    }
}


data class CreateOrganisationDTO(val name: String)
data class UserOrganisationViewModel(val organisations: List<UserOrganisationsView>)