package dev.todoplus.restapi.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Project: todoplus-restapi
 *
 * @author miroslavsafar
 * All rights reserved.
 */
@Entity
@Table(name = "subtasks")
public class SubTask {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    @Setter
    @NotNull
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    @JsonIgnore
    private Task task;

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
    @NotNull
    private Integer sort;

}
