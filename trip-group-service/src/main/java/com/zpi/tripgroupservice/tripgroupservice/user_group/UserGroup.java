package com.zpi.tripgroupservice.tripgroupservice.user_group;

import com.zpi.tripgroupservice.tripgroupservice.commons.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserGroup {

    @EmbeddedId
    private UserGroupKey id;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "votes_remaining")
    private Integer votesRemaining;


}





