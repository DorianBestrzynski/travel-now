package com.zpi.tripgroupservice.tripgroupservice.user_group;

import com.zpi.tripgroupservice.tripgroupservice.commons.Role;
import lombok.Data;

import javax.persistence.*;

@Data
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





