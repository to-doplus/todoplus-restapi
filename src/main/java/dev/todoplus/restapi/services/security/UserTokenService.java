package dev.todoplus.restapi.services.security;

import dev.todoplus.restapi.data.users.User;
import dev.todoplus.restapi.data.users.UserToken;
import dev.todoplus.restapi.repositories.UserTokenRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

import static io.jsonwebtoken.io.Encoders.BASE64;

/**
 * Project: todoplus-restapi
 *
 * @author miroslavsafar
 * All rights reserved.
 */
@Service
public class UserTokenService {

    @Autowired
    private UserTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder encoder;

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generates crypto random token of 256 bits and encode it with Base64
     *
     * @return Encoded random Token
     */
    private String generateNewToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        return BASE64.encode(randomBytes);
    }

    /**
     * Issue a new user token
     * @param user User
     * @return User Token
     */
    public String issueNewToken(User user) {
        val token = generateNewToken();
        val userToken = new UserToken(user, encoder.encode(token));
        tokenRepository.save(userToken);
        return token;
    }

}
