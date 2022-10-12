package com.zpi.transportservice.transportservice.accommodation_transport;

import com.zpi.transportservice.transportservice.dto.AccommodationInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/transport")
@RequiredArgsConstructor
public class AccommodationTransportController {

    private final AccommodationTransportService accommodationTransportService;

    private final AccommodationTransportRepository accommodationTransportRepository;
}
