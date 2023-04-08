package com.ksidelta.libruch.modules.organisation

import com.ksidelta.libruch.modules.kernel.Party
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.future.await
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.EventBus
import org.axonframework.messaging.MetaData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.KClass

@RestController
@RequestMapping("/api/organisation")
class OrganisationController(val commandGateway: CommandGateway, val eventBus: EventBus) {

    @PostMapping
    suspend fun create(user: Party.User, @RequestBody createOrganisationDTO: CreateOrganisationDTO) {
        eventBus.awaitingEvent(UserToOrganisationsModelViewUpdated::class) { correlationId ->
            createOrganisationDTO.let {
                commandGateway.send<Unit>(
                    CreateOrganisation(it.name, user),
                    MetaData(mutableMapOf(Pair("traceId", correlationId)))
                )
            }.await()
        }


    }
}

suspend fun EventBus.awaitingEvent(eventType: KClass<*>, block: suspend (correlationId: String) -> Unit) {
    val correlationId = UUID.randomUUID().toString()

    withTimeout(10000) {
        val flow =
            callbackFlow<Any> {
                val x = this
                val subscription = this@awaitingEvent.subscribe { events ->
                    events.forEach {
                        if (it.payload.javaClass == eventType.java && it.metaData["traceId"] == correlationId ) {
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