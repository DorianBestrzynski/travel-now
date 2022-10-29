package com.zpi.availabilityservice.availability;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    @Query("SELECT a FROM Availability a WHERE a.userId = ?1 AND a.groupId = ?2 AND ((a.dateFrom BETWEEN ?3 AND ?4) OR (a.dateTo BETWEEN ?3 AND ?4)) order by a.dateFrom asc")
    List<Availability> findOverlapping(Long userId, Long groupId, LocalDate dateFrom, LocalDate dateTo);

    List<Availability> findAvailabilitiesByUserIdAndGroupId(Long userId, Long groupId);
    List<Availability> findAvailabilitiesByGroupId(Long groupId);


}
