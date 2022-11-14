package com.zpi.accommodationservice.accommodationservice.votes;

import com.zpi.accommodationservice.accommodationservice.dto.AccommodationVoteDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/accommodation")
@RequiredArgsConstructor
public class AccommodationVoteController {

    private final AccommodationVoteService accommodationVoteService;

    @GetMapping("/vote")
    public ResponseEntity<List<AccommodationVote>> getVotesForAccommodation(@RequestParam Long accommodationId) {
        return ResponseEntity.ok(accommodationVoteService.getVotesForAccommodation(accommodationId));
    }

    @PostMapping("/vote")
    public ResponseEntity<AccommodationVote> vote(@RequestBody @Validated AccommodationVoteDto accommodationVoteDto) {
        return new ResponseEntity<>(accommodationVoteService.vote(accommodationVoteDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/vote")
    public ResponseEntity<AccommodationVote> deleteVote(@RequestBody @Validated AccommodationVoteId accommodationVoteId) {
        return ResponseEntity.ok(accommodationVoteService.deleteVote(accommodationVoteId));
    }
}
