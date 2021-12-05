package dev.todoplus.restapi.controllers;

import dev.todoplus.restapi.data.users.User;
import dev.todoplus.restapi.data.users.UserSettings;
import dev.todoplus.restapi.requests.update.ChangeUserSettingsRequest;
import dev.todoplus.restapi.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Project: todoplus-restapi
 *
 * @author miroslavsafar
 * All rights reserved.
 */
@RestController
@RequestMapping(value = "users", consumes = MediaType.APPLICATION_JSON_VALUE)
public class PrivateUsersController {

    @Autowired
    private UserService userService;

    @GetMapping("/settings")
    public UserSettings getUserSettings(@AuthenticationPrincipal User user) {
        return userService.getUserSettings(user);
    }

    @PutMapping("/settings")
    public UserSettings getUserSettings(@AuthenticationPrincipal User user, @RequestBody ChangeUserSettingsRequest request) {
        return userService.setUserSettings(user, request);
    }


}
