package dev.todoplus.restapi.requests.update;

import lombok.Data;

/**
 * Project: todoplus-restapi
 *
 * @author miroslavsafar
 * All rights reserved.
 */
@Data
public class UpdateTaskListRequest {

    private String displayName;
    private String color;
    private String description;

}
