package dev.todoplus.restapi.responses.dto;

import dev.todoplus.restapi.data.Importance;
import dev.todoplus.restapi.data.SubTask;
import dev.todoplus.restapi.data.TaskStatus;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

/**
 * Project: todoplus-restapi
 *
 * @author miroslavsafar
 * All rights reserved.
 */
@Data
public class TaskDTO {

    private int id;
    private String title;
    @NotNull
    private TaskStatus status;
    private Date createTime;
    private Date dueTime;
    private Date completeTime;
    @NotNull
    private Importance importance;
    private String description;
    private int sort;

    @NotNull
    private boolean myDay;
    private Set<SubTask> subTasks;
    private int taskListId;

}
