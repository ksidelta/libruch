package com.ksidelta.libruch.modules.user

import com.ksidelta.libruch.modules.kernel.Party
import org.axonframework.eventhandling.EventBus
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.net.URI
import java.util.*


/*
* Link for initiating authentication: "/oauth2/authorization/google" - info for frontend
* Callbacks: /login/oauth2/code/google - info for backend developers
*/
@Controller
@RequestMapping("/auth")
@CrossOrigin(origins = ["*"])
class UserController(val authenticationService: AuthenticationService, val eventBus: EventBus) {

    @GetMapping(path = ["/user"])
    @ResponseBody
    fun user(user: Party.User) =
        UserDTO(user.id)

    @GetMapping(path = ["/login"])
    fun loginWithRedirect(
        @RequestHeader(
            "Referer",
            defaultValue = "http://localhost"
        ) referer: String
    ): ResponseEntity<Void> =
        ResponseEntity
            .status(HttpStatus.TEMPORARY_REDIRECT)
            .location(URI.create(referer))
            .body(null)
}

data class UserDTO(val subject: UUID)
data class OathKeeperData(val subject: UUID, val extras: Map<String, String> = mapOf())