package dev.todoplus.restapi.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

/**
 * Project: todoplus-restapi
 *
 * @author miroslavsafar
 * All rights reserved.
 */
@Entity
@Table(name = "tasks")
public class Task {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    @Setter
    @NotNull
    @ManyToOne
    @JoinColumn(name = "tasklist_id", nullable = false)
    @JsonIgnore
    private TaskList taskList;

    @Getter
    @Setter
    @NotNull
    private String title;

    @Getter
    @Setter
    @NotNull
    private TaskStatus status;

    @Getter
    @Setter
    private Date createTime;

    @Getter
    @Setter
    private Date dueTime;

    @Getter
    @Setter
    private Date completeTime;

    @Getter
    @Setter
    @NotNull
    private Importance importance;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    @NotNull
    private Integer sort;

    @Getter
    @Setter
    @NotNull
    private boolean myDay;

    @Getter
    @Setter
    @OneToMany(mappedBy = "task")
    private Set<SubTask> subTasks;
}

