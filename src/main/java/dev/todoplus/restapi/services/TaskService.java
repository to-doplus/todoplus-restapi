package dev.todoplus.restapi.services;

import dev.todoplus.restapi.data.*;
import dev.todoplus.restapi.exceptions.*;
import dev.todoplus.restapi.repositories.SubTaskRepository;
import dev.todoplus.restapi.repositories.TaskListRepository;
import dev.todoplus.restapi.repositories.TaskRepository;
import dev.todoplus.restapi.repositories.UserRepository;
import dev.todoplus.restapi.requests.update.UpdateSubTaskRequest;
import dev.todoplus.restapi.requests.update.UpdateTaskListRequest;
import dev.todoplus.restapi.requests.update.UpdateTaskRequest;
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

    public Set<Task> getMyDayTasks(String username) {
        val user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            return new HashSet<>();
        }
        return taskRepository.findAllMyDayTasksByUserId(user.get().getId());
    }

    public Set<Task> getImportantTasks(String username) {
        val user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            return new HashSet<>();
        }
        return taskRepository.findAllTasksByUserIdAndImportance(user.get().getId(), Importance.HIGH);
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

    public Task createNewSubTask(int taskListId, int taskId, String title) throws TaskListDoesNotExistException {
        val taskList = taskListRepository.findById(taskListId).orElseThrow(TaskListDoesNotExistException::new);
        val task = taskRepository.findById(taskId).orElseThrow(TaskListDoesNotExistException::new);

        SubTask subTask = new SubTask();
        subTask.setTask(task);
        subTask.setTitle(title);
        subTask.setSort(0);
        subTask.setStatus(TaskStatus.INPROGRESS);

        subTaskRepository.save(subTask);

        return taskRepository.findById(taskId).orElseThrow(TaskListDoesNotExistException::new);
    }

    public Task closeTask(int taskListId, int taskId) throws TaskDoesNotExistException {
        val task = taskRepository.findById(taskId).orElseThrow(TaskDoesNotExistException::new);
        if (task.getTaskList().getId() == taskListId) {
            task.setCompleteTime(new Date());
            task.setStatus(TaskStatus.COMPLETED);

            taskRepository.save(task);
            return task;
        } else {
            throw new TaskDoesNotExistException();
        }
    }

    public Task closeSubTask(int taskListId, int taskId, int subTaskId) throws SubTaskDoesNotExistException {
        val subTask = subTaskRepository.findById(taskId).orElseThrow(SubTaskDoesNotExistException::new);
        if (subTask.getTask().getId() != taskId || subTask.getTask().getTaskList().getId() != taskListId) {
            throw new SubTaskDoesNotExistException();
        }
        subTask.setStatus(TaskStatus.COMPLETED);
        subTaskRepository.save(subTask);
        return subTask.getTask();
    }

    public Task addToMyDayList(String username, int taskId) throws TaskDoesNotExistException, NoPermissionsException {
        val task = taskRepository.findById(taskId).orElseThrow(TaskDoesNotExistException::new);
        if (!task.getTaskList().getOwner().getUsername().equals(username)) {
            throw new NoPermissionsException();
        }
        task.setMyDay(true);
        taskRepository.save(task);
        return task;
    }

    public Task removeFromMyDayList(String username, int taskId) throws TaskDoesNotExistException, NoPermissionsException {
        val task = taskRepository.findById(taskId).orElseThrow(TaskDoesNotExistException::new);
        if (!task.getTaskList().getOwner().getUsername().equals(username)) {
            throw new NoPermissionsException();
        }
        task.setMyDay(false);
        taskRepository.save(task);
        return task;
    }

    public SubTask createNewSubTask(String username, int taskId, SubTask template) throws TaskListDoesNotExistException {
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

    public TaskList updateTaskList(int taskListId, UpdateTaskListRequest request) throws TaskListDoesNotExistException {
        val taskList = taskListRepository.findById(taskListId);
        if (!taskList.isPresent()) {
            throw new TaskListDoesNotExistException();
        }

        if (request.getDescription() != null) {
            taskList.get().setDescription(request.getDescription());
        }

        if (request.getColor() != null) {
            taskList.get().setColor(request.getColor());
        }

        if (request.getDisplayName() != null) {
            taskList.get().setDisplayName(request.getDisplayName());
        }

        taskListRepository.save(taskList.get());
        return taskList.get();
    }

    public Task updateTask(int taskListId, int taskId, UpdateTaskRequest request) throws TaskDoesNotExistException {
        val task = taskRepository.findById(taskId).orElseThrow(TaskDoesNotExistException::new);
        if (task.getTaskList().getId() != taskListId) {
            throw new TaskDoesNotExistException();
        }

        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }

        if (request.getImportance() != null) {
            task.setImportance(request.getImportance());
        }

        if (request.getSort() != null) {
            task.setSort(request.getSort());
        }

        if (request.getDueTime() != null) {
            task.setDueTime(request.getDueTime());
        }

        taskRepository.save(task);
        return task;
    }

    public Task updateSubTask(int taskListId, int taskId, int subTaskId, UpdateSubTaskRequest request) throws SubTaskDoesNotExistException {
        val subTask = subTaskRepository.findById(subTaskId).orElseThrow(SubTaskDoesNotExistException::new);
        if (subTask.getTask().getId() != taskId || subTask.getTask().getTaskList().getId() != taskListId) {
            throw new SubTaskDoesNotExistException();
        }

        if (request.getTitle() != null) {
            subTask.setTitle(request.getTitle());
        }

        if (request.getSort() != null) {
            subTask.setSort(request.getSort());
        }

        subTaskRepository.save(subTask);
        return subTask.getTask();
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
