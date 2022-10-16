package com.zpi.dayplanservice.attraction;

import com.zpi.dayplanservice.dto.AttractionCandidateDto;
import com.zpi.dayplanservice.dto.AttractionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/attraction")
@RequiredArgsConstructor
public class AttractionController {
    private final AttractionService attractionService;

    @GetMapping()
    public ResponseEntity<List<Attraction>> getAllAccommodationsForDay(@RequestParam Long groupId, @RequestParam Long dayPlanId){
        var result = attractionService.getAllAttractionsForDay(groupId, dayPlanId);
        return ResponseEntity.ok(result);
    }

    @PostMapping()
    public ResponseEntity<List<AttractionCandidateDto>> addAttraction(@RequestBody AttractionDto attractionDto) {
        var attractionCandidates = attractionService.findCandidates(attractionDto);
        return ResponseEntity.ok(attractionCandidates);
    }
    @DeleteMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteAttraction(@RequestParam Long attractionId, @RequestParam Long dayPlanId){
        attractionService.deleteAccommodation(attractionId, dayPlanId);
    }
    @PatchMapping()
    public ResponseEntity<Attraction> editAccommodation(@RequestParam(name = "attractionId") Long attractionId,
                                                           @RequestParam(name = "userId") Long userId,
                                                           @RequestBody AttractionDto attractionDto){
        var result = attractionService.editAccommodation(attractionId, userId, attractionDto);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }
}
