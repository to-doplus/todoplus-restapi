package dev.todoplus.restapi.controllers;

import dev.todoplus.restapi.exceptions.UserAlreadyRegisteredException;
import dev.todoplus.restapi.requests.LoginRequest;
import dev.todoplus.restapi.requests.RegisterRequest;
import dev.todoplus.restapi.requests.TokenLoginRequest;
import dev.todoplus.restapi.responses.LoginResponse;
import dev.todoplus.restapi.services.UserService;
import dev.todoplus.restapi.services.security.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Project: todoplus-restapi
 *
 * @author miroslavsafar
 * All rights reserved.
 */

@RestController
@RequestMapping(value = "public/users", consumes = MediaType.APPLICATION_JSON_VALUE)
public class PublicUsersController {

    @Autowired
    private UserAuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    LoginResponse register(@RequestBody RegisterRequest request) {

        try {
            userService.registerUser(request.getUsername(), request.getEmail(), request.getPassword());
        } catch (UserAlreadyRegisteredException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }

        return login(new LoginRequest(request.getUsername(), request.getPassword()));
    }

    @PostMapping("/login")
    LoginResponse login(@RequestBody LoginRequest request) {

        return authenticationService
                .login(request.getUsername(), request.getPassword())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "invalid login and/or password"));
    }

    @PostMapping("/tokenlogin")
    LoginResponse login(@RequestBody TokenLoginRequest request) {

        return authenticationService
                .loginWithToken(request.getUsername(), request.getUserToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "invalid token"));
    }

}
