package com.zpi.availabilityservice.sharedGroupAvailability;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SharedGroupAvailabilityRepository extends JpaRepository<SharedGroupAvailability, Long> {
}
