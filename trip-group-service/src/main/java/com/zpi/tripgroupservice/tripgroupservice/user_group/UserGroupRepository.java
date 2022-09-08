package com.zpi.tripgroupservice.tripgroupservice.user_group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, UserGroupKey> {
}
