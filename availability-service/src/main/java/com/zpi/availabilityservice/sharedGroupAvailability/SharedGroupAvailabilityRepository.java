package com.zpi.availabilityservice.sharedGroupAvailability;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SharedGroupAvailabilityRepository extends JpaRepository<SharedGroupAvailability, Long> {

    List<SharedGroupAvailability> findAllByGroupId(Long groupId);

    @Modifying
    @Query("DELETE FROM SharedGroupAvailability s WHERE s.groupId=?1 AND s.sharedGroupAvailabilityId<>?2")
    void deleteAllByGroupIdWithoutSelected(Long groupId, Long sharedGroupAvailability);

    void deleteAllByGroupId(Long groupId);

}
