package com.zpi.accommodationservice.accommodationservice.accommodation;

import com.zpi.accommodationservice.accommodationservice.votes.AccommodationVote;
import com.zpi.accommodationservice.accommodationservice.votes.AccommodationVoteId;
import com.zpi.accommodationservice.accommodationservice.votes.AccommodationVoteRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("api/v1/accommodation")
@RequiredArgsConstructor
public class AccommodationController {
    private final AccommodationRepository accommodationRepository;

    private final AccommodationService accommodationService;

    private final AccommodationVoteRepository accommodationVoteRepository;

    @GetMapping("/addacc")
    public String addAccommodation(){
        var accomodation = new Accommodation(1L, 1L, "Tomek", "ddd", "dddd", "ddddd", "ddddd", 5, BigDecimal.ONE);
        accommodationRepository.save(accomodation);
        var accomodationVote = new AccommodationVote(new AccommodationVoteId(1L, accomodation.getAccommodationId()));
        accommodationVoteRepository.save(accomodationVote);
        return "Added accommodation";
    }
}
