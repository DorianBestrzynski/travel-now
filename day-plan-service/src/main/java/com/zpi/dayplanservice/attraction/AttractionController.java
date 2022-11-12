package com.zpi.dayplanservice.attraction;

import com.zpi.dayplanservice.dto.AttractionCandidateDto;
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

    @GetMapping("/find")
    public ResponseEntity<List<AttractionCandidateDto>> getCandidates(@RequestParam(name = "name") String name) {
        var attractionCandidates = attractionService.findCandidates(name);
        return ResponseEntity.ok(attractionCandidates);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteAttraction(@RequestParam Long attractionId, @RequestParam Long dayPlanId){
        attractionService.deleteAttraction(attractionId, dayPlanId);
    }

    @PostMapping()
    public ResponseEntity<Attraction> addAttraction(@RequestParam(name = "dayPlanId") List<Long> dayPlanIds,
                                                    @RequestParam(name = "userId") Long userId,
                                                    @RequestBody AttractionCandidateDto attractionCandidateDto){
        var result = attractionService.addAttraction(dayPlanIds, userId,  attractionCandidateDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PatchMapping()
    public ResponseEntity<Attraction> editAttraction(@RequestParam(name = "userId") Long userId,
                                                    @RequestBody Attraction attraction) {
        var result = attractionService.editAttraction(attraction);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/optimize/{dayPlanId}")
    public ResponseEntity<List<Attraction>> getOptimizedDay(@PathVariable Long dayPlanId) {
        var result = attractionService.findOptimalDayPlan(dayPlanId);
        return ResponseEntity.ok(result);
    }
}
