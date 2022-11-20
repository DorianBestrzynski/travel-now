package com.zpi.tripgroupservice.user_group;

import com.zpi.tripgroupservice.commons.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, UserGroupKey> {

    List<UserGroup> findAllById_GroupId(Long groupId);
    List<UserGroup> findAllById_GroupIdAndRoleEquals(Long id_groupId, Role role);

    Integer countAllById_GroupId(Long groupId);
}
