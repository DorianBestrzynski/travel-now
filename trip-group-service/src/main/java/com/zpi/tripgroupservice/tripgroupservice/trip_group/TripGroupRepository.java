package com.zpi.tripgroupservice.tripgroupservice.trip_group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripGroupRepository extends JpaRepository<TripGroup, Long> {

    @Query("SELECT tg FROM TripGroup tg JOIN UserGroup ug ON tg.groupId = ug.id.groupId AND ug.id.userId =?1")
    List<TripGroup> findAllGroupsForUser(Long userId);

}
