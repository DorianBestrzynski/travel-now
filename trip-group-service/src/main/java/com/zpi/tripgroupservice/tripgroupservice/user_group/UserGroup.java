package com.zpi.tripgroupservice.tripgroupservice.user_group;

import com.zpi.tripgroupservice.tripgroupservice.commons.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserGroup {

    @EmbeddedId
    private UserGroupKey id;

    @Getter
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @Getter
    private Role role;

    @Column(name = "votes_remaining")
    private Integer votesRemaining;

    public UserGroup(UserGroupKey id, Role role) {
        this.id = id;
        this.role = role;
    }
}





