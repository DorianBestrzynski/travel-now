package com.zpi.transportservice.transportservice.accommodation_transport;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccommodationTransportService {

    private final AccommodationTransportRepository accommodationTransportRepository;
}
