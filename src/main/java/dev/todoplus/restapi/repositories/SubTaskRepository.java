package dev.todoplus.restapi.repositories;

import dev.todoplus.restapi.data.SubTask;
import dev.todoplus.restapi.data.Task;
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
public interface SubTaskRepository extends JpaRepository<SubTask, Integer> {

    Set<SubTask> findAllByTask(Task task);

    void deleteSubTaskByIdAndTask(int subTaskId, Task task);

}
