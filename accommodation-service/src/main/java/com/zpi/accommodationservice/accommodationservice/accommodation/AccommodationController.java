package com.zpi.accommodationservice.accommodationservice.accommodation;

import com.zpi.accommodationservice.accommodationservice.dto.AccommodationDto;
import com.zpi.accommodationservice.accommodationservice.dto.AccommodationInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/accommodation")
@RequiredArgsConstructor
public class AccommodationController {
    private final AccommodationService accommodationService;
    @GetMapping()
    public ResponseEntity<List<Accommodation>> getAllAccommodationsForGroup(@RequestParam Long groupId){
        var result = accommodationService.getAllAccommodationsForGroup(groupId);
        return ResponseEntity.ok(result);
    }
    @PostMapping()
    public ResponseEntity<Accommodation> addAccommodation(@RequestBody AccommodationDto accommodationDto) {
        var accommodation = accommodationService.addAccommodation(accommodationDto);
        return ResponseEntity.ok(accommodation);
    }
    @DeleteMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteAccommodation(@RequestParam Long accommodationId){
        accommodationService.deleteAccommodation(accommodationId);
    }

    @PatchMapping()
    public ResponseEntity<Accommodation> editAccommodation(@RequestParam(name = "accommodationId")Long accommodationId,
                                               @RequestParam(name = "userId")Long userId,
                                               @RequestBody AccommodationDto accommodationDto){
        var result = accommodationService.editAccommodation(accommodationId, userId, accommodationDto);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }
    @GetMapping("/info")
    public ResponseEntity<AccommodationInfoDto> getAccommodationInfo(@RequestParam Long accommodationId){
        var result = accommodationService.getAccommodationInfo(accommodationId);
        return ResponseEntity.ok(result);
    }

}
