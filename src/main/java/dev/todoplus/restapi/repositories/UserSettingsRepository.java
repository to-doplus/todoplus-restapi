package dev.todoplus.restapi.repositories;

import dev.todoplus.restapi.data.users.User;
import dev.todoplus.restapi.data.users.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Project: todoplus-restapi
 *
 * @author miroslavsafar
 * All rights reserved.
 */
@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, Integer> {

    Optional<UserSettings> findOneByUser(User user);

}
