package com.ksidelta.libruch.utils

import org.axonframework.eventhandling.DomainEventMessage
import java.util.UUID
import java.util.function.BiFunction

fun <T> DomainEventMessage<T>.handle(handler: BiFunction<UUID, T, Unit>) {
    val uuid = UUID.fromString(this.aggregateIdentifier)
    val payload = this.payload

    handler.apply(uuid, payload)
}