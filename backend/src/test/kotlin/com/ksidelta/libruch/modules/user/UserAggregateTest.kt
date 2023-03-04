package com.ksidelta.libruch.modules.user

import com.ksidelta.libruch.BaseTest
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.modelling.command.AggregateStreamCreationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserAggregateTest : BaseTest() {

    @Autowired
    lateinit var commandGateway: CommandGateway

    @Test
    fun whenAggregatesWithSameDataArePersistedThenFail(): Unit = runBlocking {
        commandGateway.send<Any>(AddGoogleUser("356")).await()
        assertThrows<AggregateStreamCreationException> {
            commandGateway.send<Any>(AddGoogleUser("356")).await()
        }
    }
}