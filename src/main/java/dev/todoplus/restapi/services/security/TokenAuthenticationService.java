package dev.todoplus.restapi.services.security;

import com.google.common.collect.ImmutableMap;
import dev.todoplus.restapi.data.users.User;
import dev.todoplus.restapi.data.users.UserToken;
import dev.todoplus.restapi.repositories.UserRepository;
import dev.todoplus.restapi.responses.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Project: todoplus-restapi
 *
 * @author miroslavsafar
 * All rights reserved.
 */

@Service
final class TokenAuthenticationService implements UserAuthenticationService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JWTTokenService tokens;

    @Autowired
    UserRepository users;

    @Autowired
    UserTokenService userTokenService;

    @Override
    public Optional<LoginResponse> login(final String username, final String password) {
        Optional<User> user = users
                .findByUsername(username)
                .filter(usr -> passwordEncoder.matches(password, usr.getPassword()));
        if (!user.isPresent()) {
            return Optional.empty();
        }
        String token = tokens.permanent(ImmutableMap.of("username", username));
        String userToken = userTokenService.issueNewToken(user.get());
        return Optional.of(new LoginResponse(token, userToken, user.get().getId()));
    }

    @Override
    public Optional<LoginResponse> loginWithToken(String username, String userToken) {
        Optional<User> user = users.findByUsername(username);
        if (!user.isPresent()) {
            return Optional.empty();
        }
        Optional<UserToken> token = user.get().getTokens().stream().filter(uT -> passwordEncoder.matches(userToken, uT.getToken())).findFirst();
        if (!token.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(new LoginResponse(tokens.permanent(ImmutableMap.of("username", user.get().getUsername())), userToken, user.get().getId()));
    }

    @Override
    public Optional<User> findByToken(final String token) {
        return Optional
                .of(tokens.verify(token))
                .map(map -> map.get("username"))
                .flatMap(users::findByUsername);
    }

    @Override
    public void logout(final User user) {
        // Nothing to do
    }
}
