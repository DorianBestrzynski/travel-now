package com.zpi.tripgroupservice.tripgroupservice.user_group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class UserGroupKey implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "group_id")
    private Long groupId;

}
