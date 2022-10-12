package com.zpi.transportservice.transportservice.transport;

import com.zpi.transportservice.transportservice.commons.TransportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransportRepository extends JpaRepository<Transport, Long> {

    @Query("SELECT t FROM Transport t WHERE t.source=?1 AND t.destination=?2 AND t.startDate=?3 AND t.endDate=?4 AND t.transportType=?5")
    List<Transport> findMatchingTransport(String startingLocation, String destinationLocation, LocalDate startDate, LocalDate endDate, TransportType transportType);

    @Query("SELECT t FROM Transport t WHERE t.transportId IN ?1 AND t.transportType=?2 ")
    List<Transport> findTransportAir(List<Long> transportIds, TransportType transportType);
}
