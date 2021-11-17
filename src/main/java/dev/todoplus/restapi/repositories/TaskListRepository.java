package dev.todoplus.restapi.repositories;

import dev.todoplus.restapi.data.TaskList;
import dev.todoplus.restapi.data.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * Project: todoplus-restapi
 *
 * @author miroslavsafar
 * All rights reserved.
 */
@Repository
public interface TaskListRepository extends JpaRepository<TaskList, Integer> {

    Set<TaskList> findAllByOwner(User owner);

    Optional<TaskList> findById(int id);

}
