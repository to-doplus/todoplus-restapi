package dev.todoplus.restapi.repositories;

import dev.todoplus.restapi.data.Task;
import dev.todoplus.restapi.data.TaskList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Project: todoplus-restapi
 *
 * @author miroslavsafar
 * All rights reserved.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    Set<Task> findAllByTaskList(TaskList taskList);

}
