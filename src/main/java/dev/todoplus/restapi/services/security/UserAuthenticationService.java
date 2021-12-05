package dev.todoplus.restapi.services.security;

import dev.todoplus.restapi.data.users.User;
import dev.todoplus.restapi.responses.LoginResponse;

import java.util.Optional;

/**
 * Project: todoplus-restapi
 *
 * @author miroslavsafar
 * All rights reserved.
 */

public interface UserAuthenticationService {

    /**
     * Logs in with the given {@code username} and {@code password}.
     *
     * @param username Username
     * @param password Password
     * @return an {@link Optional} of LoginResponse
     */
    Optional<LoginResponse> login(String username, String password);

    /**
     * Logs in with the given {@code userToken}.
     *
     * @param userToken UserToken
     * @return an {@link Optional} of jwt token
     */
    Optional<LoginResponse> loginWithToken(String username, String userToken);

    /**
     * Finds a user by its jwt token.
     *
     * @param token users jwt token
     * @return user if jtw is valid
     */
    Optional<User> findByToken(String token);

    /**
     * Logs out the given input {@code user}.
     *
     * @param user the user to logout
     */
    void logout(User user);
}
