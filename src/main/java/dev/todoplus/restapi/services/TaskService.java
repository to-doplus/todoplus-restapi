package dev.todoplus.restapi.services;

import dev.todoplus.restapi.data.*;
import dev.todoplus.restapi.exceptions.TaskDoesNotExistException;
import dev.todoplus.restapi.exceptions.TaskListDoesNotExistException;
import dev.todoplus.restapi.exceptions.UserDoesNotExistException;
import dev.todoplus.restapi.repositories.SubTaskRepository;
import dev.todoplus.restapi.repositories.TaskListRepository;
import dev.todoplus.restapi.repositories.TaskRepository;
import dev.todoplus.restapi.repositories.UserRepository;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Project: todoplus-restapi
 *
 * @author miroslavsafar
 * All rights reserved.
 */
@Service
public class TaskService {

    private final TaskListRepository taskListRepository;
    private final TaskRepository taskRepository;
    private final SubTaskRepository subTaskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskListRepository taskListRepository, TaskRepository taskRepository, SubTaskRepository subTaskRepository, UserRepository userRepository) {
        this.taskListRepository = taskListRepository;
        this.taskRepository = taskRepository;
        this.subTaskRepository = subTaskRepository;
        this.userRepository = userRepository;
    }

    public Set<TaskList> getAllTaskLists(String username) {
        val user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            return new HashSet<>();
        }

        val taskList = taskListRepository.findAllByOwner(user.get());
        return taskList;
    }

    public Set<Task> getAllTasks(int taskListId) {
        val taskList = taskListRepository.getById(taskListId);
        return taskList.getTasks();
    }

    public TaskList createNewTaskList(String username, String displayName) throws UserDoesNotExistException {
        val user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            throw new UserDoesNotExistException();
        }

        TaskList taskList = new TaskList();
        taskList.setColor("#4169e1"); // Default color
        taskList.setDisplayName(displayName);
        taskList.setDescription(null);
        taskList.setOwner(user.get());

        return taskListRepository.save(taskList);
    }

    public Task createNewTask(int taskListId, String title) throws TaskListDoesNotExistException {
        val taskList = taskListRepository.findById(taskListId).orElseThrow(TaskListDoesNotExistException::new);

        Task task = new Task();
        task.setTitle(title);
        task.setStatus(TaskStatus.INPROGRESS);
        task.setDescription(null);
        task.setTaskList(taskList);
        task.setSort(0);
        task.setImportance(Importance.NORMAL);
        task.setCreateTime(new Date());

        return taskRepository.save(task);
    }

    public void closeTask(int taskListId, int taskId) throws TaskDoesNotExistException {
        val task = taskRepository.findById(taskId).orElseThrow(TaskDoesNotExistException::new);
        if (task.getTaskList().getId() == taskListId) {
            task.setCompleteTime(new Date());
            task.setStatus(TaskStatus.COMPLETED);

            taskRepository.save(task);
        } else {
            // Security eror?
        }
    }

    public SubTask createNewSubTask(int taskId, SubTask template) throws TaskListDoesNotExistException {
        val task = taskRepository.findById(taskId);
        if (!task.isPresent()) {
            throw new TaskListDoesNotExistException();
        }

        SubTask subTask = new SubTask();
        subTask.setTitle(template.getTitle());
        subTask.setStatus(template.getStatus());
        subTask.setSort(template.getSort());
        subTask.setTask(task.get());

        return subTaskRepository.save(subTask);
    }

    public void updateTaskList(int taskListId, TaskList taskListChanges) throws TaskListDoesNotExistException {
        val taskList = taskListRepository.findById(taskListId);
        if (!taskList.isPresent()) {
            throw new TaskListDoesNotExistException();
        }

        if (taskListChanges.getDescription() != null) {
            taskList.get().setDescription(taskListChanges.getDescription());
        }

        if (taskListChanges.getColor() != null) {
            taskList.get().setColor(taskListChanges.getColor());
        }

        if (taskListChanges.getDisplayName() != null) {
            taskList.get().setDisplayName(taskListChanges.getColor());
        }

        taskListRepository.save(taskList.get());

    }

    public void deleteTaskList(int taskListId) {
        taskListRepository.deleteById(taskListId);
    }

    public void deleteTask(int taskId) {
        taskRepository.deleteById(taskId);
    }

    public void deleteSubTask(int subTaskId) {
        subTaskRepository.deleteById(subTaskId);
    }


}
