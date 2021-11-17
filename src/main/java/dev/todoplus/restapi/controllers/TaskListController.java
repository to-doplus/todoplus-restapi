package dev.todoplus.restapi.controllers;

import dev.todoplus.restapi.data.Task;
import dev.todoplus.restapi.data.TaskList;
import dev.todoplus.restapi.exceptions.TaskListDoesNotExistException;
import dev.todoplus.restapi.exceptions.UserDoesNotExistException;
import dev.todoplus.restapi.requests.create.CreateTaskListRequest;
import dev.todoplus.restapi.requests.create.CreateTaskRequest;
import dev.todoplus.restapi.services.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Project: todoplus-restapi
 *
 * @author miroslavsafar
 * All rights reserved.
 */
@RestController
@RequestMapping("public/tasklists")
public class TaskListController {

    private final TaskService taskService;

    public TaskListController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public TaskList[] getAllTaskLists() {
        return taskService.getAllTaskLists("safarmirek").toArray(new TaskList[0]);
    }

    @GetMapping("/{taskListId}/tasks")
    public Task[] getTasks(@PathVariable int taskListId) {
        return taskService.getAllTasks(taskListId).toArray(new Task[0]);
    }

    @PostMapping
    public TaskList createNewTaskList(@RequestBody CreateTaskListRequest request) {
        TaskList taskList;
        try {
            taskList = taskService.createNewTaskList("safarmirek", request.getDisplayName());
        } catch (UserDoesNotExistException taskListDoesNotExistException) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task list was not found.");
        }

        return taskList;
    }

    @PostMapping("/{taskListId}/tasks")
    public Task createNewTask(@PathVariable int taskListId, @RequestBody CreateTaskRequest request) {
        Task task;
        try {
            task = taskService.createNewTask(taskListId, request.getTitle());
        } catch (TaskListDoesNotExistException taskListDoesNotExistException) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task list was not found.");
        }

        return task;
    }

}
