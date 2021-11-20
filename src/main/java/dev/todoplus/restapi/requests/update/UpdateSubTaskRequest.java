package dev.todoplus.restapi.requests.update;

import lombok.Data;

/**
 * Project: todoplus-restapi
 *
 * @author miroslavsafar
 * All rights reserved.
 */
@Data
public class UpdateSubTaskRequest {

    private String title;
    private Integer sort;

}
