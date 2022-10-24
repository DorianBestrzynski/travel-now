package com.zpi.transportservice.accommodation_transport;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationTransportService {

    private final AccommodationTransportRepository accommodationTransportRepository;

    public List<AccommodationTransport> findAccommodationTransport(Long accommodationId) {
        return accommodationTransportRepository.findAccommodationTransportByAccommodationId(accommodationId);
    }

    public void createAccommodationTransport(Long accommodationId, Long transportId) {
        AccommodationTransport accommodationTransport = new AccommodationTransport(new AccommodationTransportId(accommodationId, transportId));
        accommodationTransportRepository.save(accommodationTransport);
    }
}
