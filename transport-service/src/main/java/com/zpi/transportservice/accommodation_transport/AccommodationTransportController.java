package com.zpi.transportservice.accommodation_transport;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/transport")
@RequiredArgsConstructor
public class AccommodationTransportController {

    private final AccommodationTransportService accommodationTransportService;

    private final AccommodationTransportRepository accommodationTransportRepository;
}
