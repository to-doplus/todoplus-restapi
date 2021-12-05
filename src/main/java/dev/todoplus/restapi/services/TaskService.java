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
import dev.todoplus.restapi.responses.dto.TaskDTO;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Project: todoplus-restapi
 *
 * @author miroslavsafar
 * All rights reserved.
 */
@Service
public class TaskService {

    private final ModelMapper modelMapper = new ModelMapper();

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

    public Set<TaskDTO> getAllTasks(int taskListId) {
        val taskList = taskListRepository.getById(taskListId);
        return taskList.getTasks().stream().map(task -> modelMapper.map(task, TaskDTO.class)).collect(Collectors.toSet());
    }

    public Set<TaskDTO> getMyDayTasks(String username) {
        val user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            return new HashSet<>();
        }
        return taskRepository.findAllMyDayTasksByUserId(user.get().getId()).stream().map(task -> modelMapper.map(task, TaskDTO.class)).collect(Collectors.toSet());
    }

    public Set<TaskDTO> getImportantTasks(String username) {
        val user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            return new HashSet<>();
        }
        return taskRepository.findAllTasksByUserIdAndImportance(user.get().getId(), Importance.HIGH).stream().map(task -> modelMapper.map(task, TaskDTO.class)).collect(Collectors.toSet());
    }

    public TaskList createNewTaskList(String username, String displayName) throws UserDoesNotExistException {
        val user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            throw new UserDoesNotExistException();
        }

        if (displayName == null || displayName.equals("")) {
            throw new InvalidNameException();
        }

        TaskList taskList = new TaskList();
        taskList.setColor("#4169e1"); // Default color
        taskList.setDisplayName(displayName);
        taskList.setDescription(null);
        taskList.setOwner(user.get());

        return taskListRepository.save(taskList);
    }

    public TaskDTO createNewTask(int taskListId, String title) throws TaskListDoesNotExistException {
        val taskList = taskListRepository.findById(taskListId).orElseThrow(TaskListDoesNotExistException::new);

        if (title == null || title.equals("")) {
            throw new InvalidNameException();
        }

        Task task = new Task();
        task.setTitle(title);
        task.setStatus(TaskStatus.INPROGRESS);
        task.setDescription(null);
        task.setTaskList(taskList);
        task.setSort(taskRepository.getMaxSort(taskListId).orElse(-1) + 1);
        task.setImportance(Importance.LOW);
        task.setCreateTime(new Date());

        return taskToDTO(taskRepository.save(task));
    }

    public TaskDTO createNewSubTask(int taskListId, int taskId, String title) throws TaskListDoesNotExistException {
        val taskList = taskListRepository.findById(taskListId).orElseThrow(TaskListDoesNotExistException::new);
        val task = taskRepository.findById(taskId).orElseThrow(TaskListDoesNotExistException::new);

        if (title == null || title.equals("")) {
            throw new InvalidNameException();
        }

        SubTask subTask = new SubTask();
        subTask.setTask(task);
        subTask.setTitle(title);
        subTask.setSort(0);
        subTask.setStatus(TaskStatus.INPROGRESS);

        subTaskRepository.save(subTask);
        val ret = taskRepository.findById(taskId).orElseThrow(TaskListDoesNotExistException::new);
        return taskToDTO(ret);
    }

    public TaskDTO closeTask(int taskListId, int taskId) throws TaskDoesNotExistException {
        val task = taskRepository.findById(taskId).orElseThrow(TaskDoesNotExistException::new);
        if (task.getTaskList().getId() == taskListId) {
            task.setCompleteTime(new Date());
            task.setStatus(TaskStatus.COMPLETED);
            taskRepository.save(task);
            taskRepository.moveAllBetween(task.getTaskList().getId(), task.getSort() + 1, 1000000, -1);
            return taskToDTO(task);
        } else {
            throw new TaskDoesNotExistException();
        }
    }

    public TaskDTO reopenTask(int taskListId, int taskId) throws TaskDoesNotExistException {
        val task = taskRepository.findById(taskId).orElseThrow(TaskDoesNotExistException::new);
        if (task.getTaskList().getId() == taskListId) {
            task.setCompleteTime(null);
            task.setStatus(TaskStatus.INPROGRESS);
            task.setSort(taskRepository.getMaxSort(taskListId).orElse(-1) + 1);

            taskRepository.save(task);
            return taskToDTO(task);
        } else {
            throw new TaskDoesNotExistException();
        }
    }

    public TaskDTO closeSubTask(int taskListId, int taskId, int subTaskId) throws SubTaskDoesNotExistException {
        val subTask = subTaskRepository.findById(subTaskId).orElseThrow(SubTaskDoesNotExistException::new);
        if (subTask.getTask().getId() != taskId || subTask.getTask().getTaskList().getId() != taskListId) {
            throw new SubTaskDoesNotExistException();
        }
        subTask.setStatus(TaskStatus.COMPLETED);
        subTaskRepository.save(subTask);
        return taskToDTO(subTask.getTask());
    }

    public TaskDTO reopenSubTask(int taskListId, int taskId, int subTaskId) throws SubTaskDoesNotExistException {
        val subTask = subTaskRepository.findById(subTaskId).orElseThrow(SubTaskDoesNotExistException::new);
        if (subTask.getTask().getId() != taskId || subTask.getTask().getTaskList().getId() != taskListId) {
            throw new SubTaskDoesNotExistException();
        }
        subTask.setStatus(TaskStatus.INPROGRESS);
        subTaskRepository.save(subTask);
        return taskToDTO(subTask.getTask());
    }

    public TaskDTO addToMyDayList(String username, int taskId) throws TaskDoesNotExistException, NoPermissionsException {
        val task = taskRepository.findById(taskId).orElseThrow(TaskDoesNotExistException::new);
        if (!task.getTaskList().getOwner().getUsername().equals(username)) {
            throw new NoPermissionsException();
        }
        task.setMyDay(true);
        taskRepository.save(task);
        return taskToDTO(task);
    }

    public TaskDTO removeFromMyDayList(String username, int taskId) throws TaskDoesNotExistException, NoPermissionsException {
        val task = taskRepository.findById(taskId).orElseThrow(TaskDoesNotExistException::new);
        if (!task.getTaskList().getOwner().getUsername().equals(username)) {
            throw new NoPermissionsException();
        }
        task.setMyDay(false);
        taskRepository.save(task);
        return taskToDTO(task);
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

    @Transactional
    public TaskDTO updateTask(int taskListId, int taskId, UpdateTaskRequest request) throws TaskDoesNotExistException {
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
            int sourceSort = task.getSort();
            if(!task.getSort().equals(request.getSort())){
                if (sourceSort < request.getSort()) { // UP
                    taskRepository.moveAllBetween(task.getTaskList().getId(), sourceSort + 1, request.getSort(), -1);
                } else { // DOWN
                    taskRepository.moveAllBetween(task.getTaskList().getId(), request.getSort(), sourceSort - 1, 1);
                }

                task.setSort(request.getSort());
                taskRepository.setSort(taskId, request.getSort());
                return taskToDTO(task);
            }
        }

        if (request.getDueTime() != null) {
            task.setDueTime(request.getDueTime());
        }

        taskRepository.save(task);
        return taskToDTO(task);
    }

    public TaskDTO removeDueDate(int taskListId, int taskId) throws TaskDoesNotExistException {
        val task = taskRepository.findById(taskId).orElseThrow(TaskDoesNotExistException::new);
        if (task.getTaskList().getId() != taskListId) {
            throw new TaskDoesNotExistException();
        }
        task.setDueTime(null);

        taskRepository.save(task);
        return taskToDTO(task);
    }

    public TaskDTO updateSubTask(int taskListId, int taskId, int subTaskId, UpdateSubTaskRequest request) throws SubTaskDoesNotExistException {
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
        return taskToDTO(subTask.getTask());
    }

    public void deleteTaskList(int taskListId) {
        taskListRepository.deleteById(taskListId);
    }


    public void deleteTask(int taskListId, int taskId) throws TaskDoesNotExistException {
        val task = taskRepository.findById(taskId).orElseThrow(TaskDoesNotExistException::new);
        if (task.getTaskList().getId() != taskListId) {
            throw new TaskDoesNotExistException();
        }
        taskRepository.deleteById(taskId);
        taskRepository.moveAllBetween(task.getTaskList().getId(), task.getSort() + 1, 1000000, -1);
    }

    public TaskDTO deleteSubTaskAndGet(int taskListId, int taskId, int subTaskId) throws SubTaskDoesNotExistException {
        val subTask = subTaskRepository.findById(subTaskId).orElseThrow(SubTaskDoesNotExistException::new);
        if (subTask.getTask().getId() != taskId || subTask.getTask().getTaskList().getId() != taskListId) {
            throw new SubTaskDoesNotExistException();
        }
        subTaskRepository.delete(subTask);
        val task = taskRepository.getById(taskId);
        return taskToDTO(task);
    }

    public TaskDTO taskToDTO(Task task) {
        TaskDTO dto = modelMapper.map(task, TaskDTO.class);
        dto.setTaskListId(task.getTaskList().getId());
        if (dto.getSubTasks() == null) {
            dto.setSubTasks(new HashSet<>());
        }
        return dto;
    }
}
