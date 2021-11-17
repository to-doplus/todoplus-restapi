package dev.todoplus.restapi.services;

import dev.todoplus.restapi.data.users.User;
import dev.todoplus.restapi.exceptions.UserAlreadyRegisteredException;
import dev.todoplus.restapi.repositories.UserRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Project: todoplus-restapi
 *
 * @author miroslavsafar
 * All rights reserved.
 */
@Service
public class UserService {

    @Autowired
    UserRepository repository;

    @Autowired
    PasswordEncoder encoder;

    public User registerUser(String username, String email, String password) throws UserAlreadyRegisteredException {
        val user = repository.findByUsername(username);
        if (user.isPresent()) {
            throw new UserAlreadyRegisteredException();
        }
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(encoder.encode(password));
        repository.save(new User());
        return newUser;
    }

}
