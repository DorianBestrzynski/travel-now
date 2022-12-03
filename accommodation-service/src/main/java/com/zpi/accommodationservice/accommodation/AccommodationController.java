package com.zpi.accommodationservice.accommodation;

import com.zpi.accommodationservice.dto.AccommodationDto;
import com.zpi.accommodationservice.dto.AccommodationInfoDto;
import com.zpi.accommodationservice.dto.AccommodationWithVotesDto;
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

    @GetMapping("/list")
    public ResponseEntity<List<Accommodation>> getAllAccommodationsForGroup(@RequestParam Long groupId, @RequestParam(required = false) Long userId){
        var result = accommodationService.getAllAccommodationsForGroup(groupId, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/votes")
    public ResponseEntity<List<AccommodationWithVotesDto>> getAllAccommodationsForGroupWithVotes(@RequestParam Long groupId){
        long start = System.currentTimeMillis();
        var result = accommodationService.getAllAccommodationsForGroupWithVotes(groupId);
        long finish = System.currentTimeMillis();
        System.out.println(finish - start);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/votes/user")
    public ResponseEntity<List<AccommodationWithVotesDto>> getUserVotes(@RequestParam Long userId, @RequestParam Long groupId) {
        return ResponseEntity.ok(accommodationService.getUserVotes(userId, groupId));
    }

    @PostMapping()
    public ResponseEntity<Accommodation> addAccommodation(@RequestBody AccommodationDto accommodationDto) {
        var accommodation = accommodationService.addAccommodation(accommodationDto);
        return ResponseEntity.ok(accommodation);
    }


    @PatchMapping("/accept")
    public void acceptAccommodation(@RequestParam Long accommodationId) {
        accommodationService.acceptAccommodation(accommodationId);
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

    @GetMapping()
    public ResponseEntity<Accommodation> getAccommodation(@RequestParam Long accommodationId){
        var result = accommodationService.getAccommodation(accommodationId);
        return ResponseEntity.ok(result);
    }

}
