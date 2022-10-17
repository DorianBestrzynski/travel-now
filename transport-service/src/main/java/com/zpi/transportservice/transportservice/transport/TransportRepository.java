package com.zpi.transportservice.transportservice.transport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransportRepository extends JpaRepository<Transport, Long> {

    @Query("SELECT at FROM AirTransport at WHERE at.source=?1 AND at.destination=?2 AND at.startDate=?3")
    List<AirTransport> findMatchingAirTransport(String startingLocation, String destinationLocation, LocalDate startDate);

    @Query("SELECT ct FROM CarTransport ct WHERE ct.source=?1 AND ct.destination=?2 AND ct.startDate=?3")
    List<CarTransport> findMatchingCarTransport(String startingLocation, String destinationLocation, LocalDate startDate);

    @Query("SELECT at FROM AirTransport at WHERE at.transportId IN ?1")
    List<AirTransport> findAirTransport(List<Long> transportIds);

    @Query("SELECT ct FROM CarTransport ct WHERE ct.transportId IN ?1")
    List<CarTransport> findCarTransport(List<Long> transportIds);

    @Query("SELECT ut FROM UserTransport ut WHERE ut.transportId IN ?1")
    List<UserTransport> findUserTransport(List<Long> transportIds);
}
