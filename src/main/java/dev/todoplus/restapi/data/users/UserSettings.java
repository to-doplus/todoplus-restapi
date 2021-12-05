package dev.todoplus.restapi.data.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Project: todoplus-restapi
 *
 * @author miroslavsafar
 * All rights reserved.
 */
@NoArgsConstructor
@Entity
@Table(name = "user_settings")
public class UserSettings {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Integer id;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Getter
    @Setter
    private boolean myDayEnabled;

    @Getter
    @Setter
    private boolean importantEnabled;

}
