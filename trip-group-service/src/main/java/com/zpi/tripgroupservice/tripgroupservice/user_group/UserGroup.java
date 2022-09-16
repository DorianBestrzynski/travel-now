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
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "votes_remaining", nullable = false)
    private Integer votesRemaining;

    public UserGroup(UserGroupKey id, Role role) {
        this.id = id;
        this.role = role;
    }
}





