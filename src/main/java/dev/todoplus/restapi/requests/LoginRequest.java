package dev.todoplus.restapi.requests;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Project: mminer
 *
 * @author miroslavsafar
 * All rights reserved.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotNull
    String username;
    @NotNull
    String password;
}
