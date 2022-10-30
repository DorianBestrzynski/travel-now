package com.zpi.availabilityservice.sharedGroupAvailability;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SharedGroupAvailabilityRepository extends JpaRepository<SharedGroupAvailability, Long> {

    List<SharedGroupAvailability> findAllByGroupId(Long groupId);

    void deleteAllByGroupId(Long groupId);
}
