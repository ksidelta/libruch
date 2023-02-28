package com.ksidelta.libruch

import org.axonframework.commandhandling.CommandBus
import org.axonframework.commandhandling.SimpleCommandBus
import org.axonframework.common.transaction.TransactionManager
import org.axonframework.config.Configurer
import org.axonframework.config.ConfigurerModule
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore
import org.axonframework.eventsourcing.eventstore.EventStorageEngine
import org.axonframework.eventsourcing.eventstore.EventStore
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine
import org.axonframework.messaging.interceptors.CorrelationDataInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class AxonConfig {
    @Bean
    fun eventStore(
        storageEngine: EventStorageEngine?,
    ): EventStore {
        return EmbeddedEventStore.builder()
            .storageEngine(storageEngine)
            .build()
    }

    @Bean
    fun storageEngine(): EventStorageEngine {
        return InMemoryEventStorageEngine()
    }

    // omitting other configuration methods...
    @Bean
    fun simpleCommandBus(
        transactionManager: TransactionManager,
        //spanFactory: SpanFactory?
    ): CommandBus? {
        return SimpleCommandBus.builder()
            .transactionManager(transactionManager)
            //.messageMonitor(metricRegistry.registerCommandBus("commandBus"))
            //.spanFactory(spanFactory!!) // ...
            .build()
    }

    @Bean
    fun commandBusCorrelationConfigurerModule(): ConfigurerModule? {
        return ConfigurerModule { configurer: Configurer ->
            configurer.onInitialize { config: org.axonframework.config.Configuration ->
                config.commandBus().registerHandlerInterceptor(
                    CorrelationDataInterceptor(config.correlationDataProviders())
                )
            }
        }
    }
}