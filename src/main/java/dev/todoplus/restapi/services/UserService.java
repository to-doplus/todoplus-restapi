package dev.todoplus.restapi.services;

import dev.todoplus.restapi.data.users.User;
import dev.todoplus.restapi.data.users.UserSettings;
import dev.todoplus.restapi.exceptions.UserAlreadyRegisteredException;
import dev.todoplus.restapi.repositories.UserRepository;
import dev.todoplus.restapi.repositories.UserSettingsRepository;
import dev.todoplus.restapi.requests.update.ChangeUserSettingsRequest;
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
    UserSettingsRepository userSettingsRepository;

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
        repository.save(newUser);
        return newUser;
    }

    public UserSettings getUserSettings(User user) {
        val userSettings = userSettingsRepository.findOneByUser(user);
        if (userSettings.isPresent()) {
            return userSettings.get();
        }
        UserSettings settings = new UserSettings();
        settings.setUser(user);
        settings.setMyDayEnabled(true);
        settings.setImportantEnabled(true);
        return userSettingsRepository.save(settings);
    }

    public UserSettings setUserSettings(User user, ChangeUserSettingsRequest request) {
        val userSettings = userSettingsRepository.findOneByUser(user);
        if (!userSettings.isPresent()) {
            UserSettings settings = new UserSettings();
            settings.setUser(user);
            settings.setMyDayEnabled(request.getMyDayEnabled());
            settings.setImportantEnabled(request.getImportantEnabled());
            return userSettingsRepository.save(settings);
        }

        if (request.getMyDayEnabled() != null) {
            userSettings.get().setMyDayEnabled(request.getMyDayEnabled());
        }

        if (request.getImportantEnabled() != null) {
            userSettings.get().setImportantEnabled(request.getImportantEnabled());
        }

        return userSettingsRepository.save(userSettings.get());
    }

}
