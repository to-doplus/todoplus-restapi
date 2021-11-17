package dev.todoplus.restapi.repositories;

import dev.todoplus.restapi.data.users.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Project: mminer
 *
 * @author miroslavsafar
 * All rights reserved.
 */
@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Integer> {

}
