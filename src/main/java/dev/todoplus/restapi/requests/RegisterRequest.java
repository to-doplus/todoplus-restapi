package dev.todoplus.restapi.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Project: mminer
 *
 * @author miroslavsafar
 * All rights reserved.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotNull
    private String username;

    @NotNull
    private String email;

    @NotNull
    private String password;
}
