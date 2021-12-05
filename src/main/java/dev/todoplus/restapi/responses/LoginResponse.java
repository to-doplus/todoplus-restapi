package dev.todoplus.restapi.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Project: todoplus-restapi
 *
 * @author miroslavsafar
 * All rights reserved.
 */
@Data
@AllArgsConstructor
public class LoginResponse {

    @NotNull
    String token;
    String userToken;
    int userId;
}
