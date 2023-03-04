package com.ksidelta.libruch.modules.user

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/user")
class UserController(val userService: UserService) {

    @GetMapping
    fun user(principal: Principal) =
        userService.findUser(principal)

}