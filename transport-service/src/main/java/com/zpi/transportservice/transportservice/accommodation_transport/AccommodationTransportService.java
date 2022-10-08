package com.zpi.transportservice.transportservice.accommodation_transport;

import com.zpi.transportservice.transportservice.dto.AccommodationInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationTransportService {

    private final AccommodationTransportRepository accommodationTransportRepository;

}
