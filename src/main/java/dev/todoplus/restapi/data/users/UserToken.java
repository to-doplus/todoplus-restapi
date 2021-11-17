package dev.todoplus.restapi.data.users;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * Project: exitgame
 *
 * @author miroslavsafar
 * All rights reserved.
 */

@NoArgsConstructor
@Entity
@Table(name = "user_tokens")
public class UserToken {

    public UserToken(User user, String token) {
        this.user = user;
        this.token = token;
    }

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Getter
    @Setter
    private String token;

    @Getter
    private Date created;

    @PrePersist
    protected void onCreate() {
        created = new Date();
    }

}
