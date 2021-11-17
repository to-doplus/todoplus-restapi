package dev.todoplus.restapi.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Project: mminer
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
