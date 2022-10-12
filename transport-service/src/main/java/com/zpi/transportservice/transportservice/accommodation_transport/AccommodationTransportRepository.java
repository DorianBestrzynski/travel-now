package com.zpi.transportservice.transportservice.accommodation_transport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccommodationTransportRepository extends JpaRepository<AccommodationTransport, AccommodationTransportId> {

    @Query("SELECT at FROM AccommodationTransport at WHERE at.id.accommodationId =?1")
    List<AccommodationTransport> findAccommodationTransportByAccommodationId(Long accommodationId);
}
