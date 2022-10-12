package com.zpi.transportservice.transportservice.transport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransportRepository extends JpaRepository<Transport, Long> {

    @Query("SELECT at FROM AirTransport at WHERE at.source=?1 AND at.destination=?2 AND at.startDate=?3")
    List<AirTransport> findMatchingTransportAir(String startingLocation, String destinationLocation, LocalDate startDate);

    @Query("SELECT at FROM AirTransport at WHERE at.transportId IN ?1")
    List<AirTransport> findTransportAir(List<Long> transportIds);
}
