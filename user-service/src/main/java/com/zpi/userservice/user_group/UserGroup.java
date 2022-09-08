package com.zpi.userservice.user_group;
import com.zpi.userservice.commons.Role;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class UserGroup {

    @EmbeddedId
    private UserGroupKey id;

    @Column(name = "role")
    private Role role;

    @Column(name = "votes_remaining")
    private Integer votesRemaining;


}




