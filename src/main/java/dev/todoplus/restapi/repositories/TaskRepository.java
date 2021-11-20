package dev.todoplus.restapi.repositories;

import dev.todoplus.restapi.data.Importance;
import dev.todoplus.restapi.data.Task;
import dev.todoplus.restapi.data.TaskList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT t FROM Task t WHERE t.taskList.owner.id = :userId AND t.myDay = true")
    Set<Task> findAllMyDayTasksByUserId(@Param("userId") int userId);

    @Query("SELECT t FROM Task t WHERE t.taskList.owner.id = :userId AND t.importance = :importance")
    Set<Task> findAllTasksByUserIdAndImportance(@Param("userId") int userId, @Param("importance")Importance importance);

}
