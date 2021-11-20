package dev.todoplus.restapi.controllers;

import dev.todoplus.restapi.data.Task;
import dev.todoplus.restapi.data.TaskList;
import dev.todoplus.restapi.exceptions.NoPermissionsException;
import dev.todoplus.restapi.exceptions.TaskDoesNotExistException;
import dev.todoplus.restapi.exceptions.TaskListDoesNotExistException;
import dev.todoplus.restapi.exceptions.UserDoesNotExistException;
import dev.todoplus.restapi.requests.create.CreateSubTaskRequest;
import dev.todoplus.restapi.requests.create.CreateTaskListRequest;
import dev.todoplus.restapi.requests.create.CreateTaskRequest;
import dev.todoplus.restapi.requests.update.UpdateSubTaskRequest;
import dev.todoplus.restapi.requests.update.UpdateTaskListRequest;
import dev.todoplus.restapi.requests.update.UpdateTaskRequest;
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

    @PutMapping("/{taskListId}")
    public TaskList updateTaskList(@PathVariable int taskListId, @RequestBody UpdateTaskListRequest request) {
        try {
            return taskService.updateTaskList(taskListId, request);
        } catch (TaskListDoesNotExistException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task list was not found.");
        }
    }

    @GetMapping("/{taskListId}/tasks")
    public Task[] getTasks(@PathVariable int taskListId) {
        return taskService.getAllTasks(taskListId).toArray(new Task[0]);
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

    @PutMapping("/{taskListId}/tasks/{taskId}")
    public Task updateTask(@PathVariable int taskListId, @PathVariable int taskId, @RequestBody UpdateTaskRequest request) {
        Task task;
        try {
            task = taskService.updateTask(taskListId, taskId, request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task was not found.");
        }

        return task;
    }

    @PutMapping("/{taskListId}/tasks/{taskId}/close")
    public Task closeTask(@PathVariable int taskListId, @PathVariable int taskId) {
        Task task;
        try {
            task = taskService.closeTask(taskListId, taskId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task was not found.");
        }
        return task;
    }

    @PostMapping("/{taskListId}/tasks/{taskId}/subtasks")
    public Task createNewSubTask(@PathVariable int taskListId, @PathVariable int taskId, @RequestBody CreateSubTaskRequest request) {
        Task task;
        try {
            task = taskService.createNewSubTask(taskListId, taskId, request.getTitle());
        } catch (TaskListDoesNotExistException taskListDoesNotExistException) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task list was not found.");
        }

        return task;
    }

    @PutMapping("/{taskListId}/tasks/{taskId}/subtasks/{subTaskId}")
    public Task updateSubTask(@PathVariable int taskListId, @PathVariable int taskId, @PathVariable int subTaskId, @RequestBody UpdateSubTaskRequest request) {
        Task task;
        try {
            task = taskService.updateSubTask(taskListId, taskId, subTaskId, request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subtask was not found.");
        }

        return task;
    }

    @PutMapping("/{taskListId}/tasks/{taskId}/subtasks/{subTaskId}/close")
    public Task closeSubTask(@PathVariable int taskListId, @PathVariable int taskId, @PathVariable int subTaskId) {
        Task task;
        try {
            task = taskService.closeSubTask(taskListId, taskId, subTaskId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subtask was not found.");
        }

        return task;
    }

    // ---- My Day Endpoints ----

    @GetMapping("/c/myday/tasks")
    public Task[] getMyDayTasks() {
        return taskService.getMyDayTasks("safarmirek").toArray(new Task[0]);
    }

    @PutMapping("/c/myday/tasks/add/{taskId}")
    public Task addToMyDay(@PathVariable int taskId) {
        try {
            return taskService.addToMyDayList("safarmirek", taskId);
        } catch (TaskDoesNotExistException | NoPermissionsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You do not have permissions to do that.");
        }
    }

    @PutMapping("/c/myday/tasks/remove/{taskId}")
    public Task removeFromMyDay(@PathVariable int taskId) {
        try {
            return taskService.removeFromMyDayList("safarmirek", taskId);
        } catch (TaskDoesNotExistException | NoPermissionsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You do not have permissions to do that.");
        }
    }

    // ---- Important Tasks Endpoints ----
    @GetMapping("/c/important/tasks")
    public Task[] getImportantTasks() {
        return taskService.getImportantTasks("safarmirek").toArray(new Task[0]);
    }


}
