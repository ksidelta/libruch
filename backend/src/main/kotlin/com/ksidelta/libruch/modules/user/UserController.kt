package com.ksidelta.libruch.modules.user

import com.ksidelta.libruch.modules.kernel.Party
import org.axonframework.eventhandling.EventBus
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.security.Principal
import java.util.*



/*
* Link for initiating authentication: "/oauth2/authorization/google" - info for frontend
* Callbacks: /login/oauth2/code/google - info for backend developers
*/
@RestController
@RequestMapping("/auth")
class UserController(val userService: UserService, val eventBus: EventBus) {

    @GetMapping(path = ["/user"])
    fun user(user: Party.User) =
        UserDTO(user.id)

    fun ksi(){
        val x =eventBus.subscribe({

        })
        x.cancel()
    }
}

data class UserDTO(val subject: UUID)
data class OathKeeperData(val subject: UUID, val extras: Map<String, String> = mapOf())