package dev.todoplus.restapi.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.todoplus.restapi.data.users.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Project: todoplus-restapi
 *
 * @author miroslavsafar
 * All rights reserved.
 */
@Entity
@Table(name = "tasklists")
public class TaskList {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    @Setter
    @NotNull
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonIgnore
    private User owner;

    @Getter
    @Setter
    @NotNull
    private String displayName;

    @Getter
    @Setter
    @NotNull
    private String color;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    @OneToMany(mappedBy = "taskList")
    @JsonIgnore
    private Set<Task> tasks;

}
