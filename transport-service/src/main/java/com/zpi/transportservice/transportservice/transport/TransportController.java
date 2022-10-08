package com.zpi.transportservice.transportservice.transport;

import com.zpi.transportservice.transportservice.accommodation_transport.AccommodationTransport;
import com.zpi.transportservice.transportservice.accommodation_transport.AccommodationTransportId;
import com.zpi.transportservice.transportservice.accommodation_transport.AccommodationTransportRepository;
import com.zpi.transportservice.transportservice.commons.TransportType;
import com.zpi.transportservice.transportservice.dto.AccommodationInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("api/v1/transport")
@RequiredArgsConstructor
public class TransportController {

    private final TransportRepository transportRepository;

    private final TransportService transportService;

    private final AccommodationTransportRepository accommodationTransportRepository;

    @GetMapping("/addtran")
    public String addTransport(){
        var transport = new Transport(TransportType.CAR, Duration.ZERO, BigDecimal.ONE, "Barcelona", "Madrid", "https://dadadada.com");
        transportRepository.save(transport);
        var accTrans = new AccommodationTransport(new AccommodationTransportId(transport.getTransportId(),1L));
        accommodationTransportRepository.save(accTrans);
        return "Added transport and accommodation transport";
    }

    @PostMapping
    public ResponseEntity<List<AccommodationTransport>> generateTransportForAccommodation(AccommodationInfoDto accommodationInfoDto){
        var generatedMeansOfTransport = transportService.generateTransportForAccommodation(accommodationInfoDto);
        return new ResponseEntity<>(generatedMeansOfTransport, HttpStatus.CREATED);
    }


}
