package dev.todoplus.restapi.controllers;

import dev.todoplus.restapi.data.TaskList;
import dev.todoplus.restapi.data.users.User;
import dev.todoplus.restapi.exceptions.*;
import dev.todoplus.restapi.requests.create.CreateSubTaskRequest;
import dev.todoplus.restapi.requests.create.CreateTaskListRequest;
import dev.todoplus.restapi.requests.create.CreateTaskRequest;
import dev.todoplus.restapi.requests.update.UpdateSubTaskRequest;
import dev.todoplus.restapi.requests.update.UpdateTaskListRequest;
import dev.todoplus.restapi.requests.update.UpdateTaskRequest;
import dev.todoplus.restapi.responses.dto.TaskDTO;
import dev.todoplus.restapi.services.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Project: todoplus-restapi
 *
 * @author miroslavsafar
 * All rights reserved.
 */
@RestController
@RequestMapping("tasklists")
public class TaskListController {

    private final TaskService taskService;

    public TaskListController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public TaskList[] getAllTaskLists(@AuthenticationPrincipal User user) {
        return taskService.getAllTaskLists(user.getUsername()).toArray(new TaskList[0]);
    }

    @PostMapping
    public TaskList createNewTaskList(@AuthenticationPrincipal User user, @RequestBody CreateTaskListRequest request) {
        TaskList taskList;
        try {
            taskList = taskService.createNewTaskList(user.getUsername(), request.getDisplayName());
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

    @DeleteMapping("/{taskListId}")
    public void deleteTask(@PathVariable int taskListId) {
        taskService.deleteTaskList(taskListId);
    }

    @GetMapping("/{taskListId}/tasks")
    public TaskDTO[] getTasks(@PathVariable int taskListId) {
        return taskService.getAllTasks(taskListId).toArray(new TaskDTO[0]);
    }

    @PostMapping("/{taskListId}/tasks")
    public TaskDTO createNewTask(@PathVariable int taskListId, @RequestBody CreateTaskRequest request) {
        TaskDTO task;
        try {
            task = taskService.createNewTask(taskListId, request.getTitle());
        } catch (TaskListDoesNotExistException taskListDoesNotExistException) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task list was not found.");
        }

        return task;
    }

    @PutMapping("/{taskListId}/tasks/{taskId}")
    public TaskDTO updateTask(@PathVariable int taskListId, @PathVariable int taskId, @RequestBody UpdateTaskRequest request) {
        TaskDTO task;
        try {
            task = taskService.updateTask(taskListId, taskId, request);
        } catch (TaskDoesNotExistException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task was not found.");
        }

        return task;
    }

    @PutMapping("/{taskListId}/tasks/{taskId}/close")
    public TaskDTO closeTask(@PathVariable int taskListId, @PathVariable int taskId) {
        TaskDTO task;
        try {
            task = taskService.closeTask(taskListId, taskId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task was not found.");
        }
        return task;
    }

    @PutMapping("/{taskListId}/tasks/{taskId}/reopen")
    public TaskDTO reopenTask(@PathVariable int taskListId, @PathVariable int taskId) {
        TaskDTO task;
        try {
            task = taskService.reopenTask(taskListId, taskId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task was not found.");
        }
        return task;
    }

    @DeleteMapping("/{taskListId}/tasks/{taskId}")
    public void deleteTask(@PathVariable int taskListId, @PathVariable int taskId) {
        try {
            taskService.deleteTask(taskListId, taskId);
        } catch (TaskDoesNotExistException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task was not found");
        }
    }

    @DeleteMapping("/{taskListId}/tasks/{taskId}/duedate")
    public TaskDTO deleteTaskDueDate(@PathVariable int taskListId, @PathVariable int taskId) {
        try {
            return taskService.removeDueDate(taskListId, taskId);
        } catch (TaskDoesNotExistException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task was not found");
        }
    }

    @PostMapping("/{taskListId}/tasks/{taskId}/subtasks")
    public TaskDTO createNewSubTask(@PathVariable int taskListId, @PathVariable int taskId, @RequestBody CreateSubTaskRequest request) {
        TaskDTO task;
        try {
            task = taskService.createNewSubTask(taskListId, taskId, request.getTitle());
        } catch (TaskListDoesNotExistException taskListDoesNotExistException) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task list was not found.");
        }

        return task;
    }

    @PutMapping("/{taskListId}/tasks/{taskId}/subtasks/{subTaskId}")
    public TaskDTO updateSubTask(@PathVariable int taskListId, @PathVariable int taskId, @PathVariable int subTaskId, @RequestBody UpdateSubTaskRequest request) {
        TaskDTO task;
        try {
            task = taskService.updateSubTask(taskListId, taskId, subTaskId, request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subtask was not found.");
        }

        return task;
    }

    @PutMapping("/{taskListId}/tasks/{taskId}/subtasks/{subTaskId}/close")
    public TaskDTO closeSubTask(@PathVariable int taskListId, @PathVariable int taskId, @PathVariable int subTaskId) {
        TaskDTO task;
        try {
            task = taskService.closeSubTask(taskListId, taskId, subTaskId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subtask was not found.");
        }

        return task;
    }

    @PutMapping("/{taskListId}/tasks/{taskId}/subtasks/{subTaskId}/reopen")
    public TaskDTO reopenSubTask(@PathVariable int taskListId, @PathVariable int taskId, @PathVariable int subTaskId) {
        TaskDTO task;
        try {
            task = taskService.reopenSubTask(taskListId, taskId, subTaskId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subtask was not found.");
        }

        return task;
    }

    @DeleteMapping("/{taskListId}/tasks/{taskId}/subtasks/{subTaskId}")
    public TaskDTO deleteSubTask(@PathVariable int taskListId, @PathVariable int taskId, @PathVariable int subTaskId) {
        try {
            return taskService.deleteSubTaskAndGet(taskListId, taskId, subTaskId);
        } catch (SubTaskDoesNotExistException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subtask was not found");
        }
    }

    // ---- My Day Endpoints ----

    @GetMapping("/c/myday/tasks")
    public TaskDTO[] getMyDayTasks(@AuthenticationPrincipal User user) {
        return taskService.getMyDayTasks(user.getUsername()).toArray(new TaskDTO[0]);
    }

    @PutMapping("/c/myday/tasks/add/{taskId}")
    public TaskDTO addToMyDay(@AuthenticationPrincipal User user, @PathVariable int taskId) {
        try {
            return taskService.addToMyDayList(user.getUsername(), taskId);
        } catch (TaskDoesNotExistException | NoPermissionsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You do not have permissions to do that.");
        }
    }

    @PutMapping("/c/myday/tasks/remove/{taskId}")
    public TaskDTO removeFromMyDay(@AuthenticationPrincipal User user, @PathVariable int taskId) {
        try {
            return taskService.removeFromMyDayList(user.getUsername(), taskId);
        } catch (TaskDoesNotExistException | NoPermissionsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You do not have permissions to do that.");
        }
    }

    // ---- Important Tasks Endpoints ----
    @GetMapping("/c/important/tasks")
    public TaskDTO[] getImportantTasks(@AuthenticationPrincipal User user) {
        return taskService.getImportantTasks(user.getUsername()).toArray(new TaskDTO[0]);
    }


}
