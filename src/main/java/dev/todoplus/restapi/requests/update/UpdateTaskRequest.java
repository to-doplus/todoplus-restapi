package dev.todoplus.restapi.requests.update;

import dev.todoplus.restapi.data.Importance;
import lombok.Data;

import java.util.Date;

/**
 * Project: todoplus-restapi
 *
 * @author miroslavsafar
 * All rights reserved.
 */
@Data
public class UpdateTaskRequest {

    private String title;
    private Date dueTime;
    private Integer sort;
    private Importance importance;
    private String description;

}
