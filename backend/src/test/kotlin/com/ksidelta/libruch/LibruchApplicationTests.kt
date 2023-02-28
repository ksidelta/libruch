package com.ksidelta.libruch

import com.ksidelta.libruch.modules.example.NewBookRegistered
import com.ksidelta.libruch.modules.example.RegisterNewBook
import com.ksidelta.libruch.modules.kernel.Party
import org.axonframework.commandhandling.CommandBus
import org.axonframework.commandhandling.gateway.CommandGateway
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*

@SpringBootTest
@Testcontainers()
class LibruchApplicationTests {

    companion object{
        val postgres =
    }

    @Autowired
    lateinit var commandGateway: CommandGateway

    @Test
    fun contextLoads() {
        commandGateway.send<Nothing>(RegisterNewBook("", Party(UUID.randomUUID())))
    }

}
