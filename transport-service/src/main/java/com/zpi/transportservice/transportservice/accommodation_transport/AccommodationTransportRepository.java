package com.zpi.transportservice.transportservice.accommodation_transport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccommodationTransportRepository extends JpaRepository<AccommodationTransport, AccommodationTransportId> {
}
