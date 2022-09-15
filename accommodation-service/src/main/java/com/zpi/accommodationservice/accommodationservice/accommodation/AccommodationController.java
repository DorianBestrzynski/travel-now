package com.zpi.accommodationservice.accommodationservice.accommodation;

import com.zpi.accommodationservice.accommodationservice.dto.AccommodationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/accommodation")
@RequiredArgsConstructor
public class AccommodationController {
    private final AccommodationService accommodationService;
    @PostMapping()
    public ResponseEntity<Accommodation> addAccommodation(@RequestBody AccommodationDto accommodationDto) {
        var accommodation = accommodationService.addAccommodation(accommodationDto);
        return ResponseEntity.ok(accommodation);
    }
}
