package dev.todoplus.restapi.repositories;

import dev.todoplus.restapi.data.Importance;
import dev.todoplus.restapi.data.Task;
import dev.todoplus.restapi.data.TaskList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
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

    @Transactional
    @Modifying
    @Query("UPDATE Task t SET t.sort = t.sort + :amount WHERE t.taskList.id= :taskListId AND t.sort >= :startSort AND t.sort <= :endSort AND t.status = 0")
    void moveAllBetween(@Param("taskListId") int taskListId, @Param("startSort") int startSort, @Param("endSort") int endSort, @Param("amount") int amount);

    @Transactional
    @Modifying
    @Query("UPDATE Task t SET t.sort = :sort WHERE t.id = :taskId")
    void setSort(@Param("taskId") int taskId, @Param("sort") int sort);

    @Query("SELECT MAX(t.sort) FROM Task t WHERE t.taskList.id = :taskListId AND t.status = 0")
    Optional<Integer> getMaxSort(@Param("taskListId") int taskListId);

}
