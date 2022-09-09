package com.zpi.accommodationservice.accommodationservice.votes;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/accommodation")
@RequiredArgsConstructor
public class AccommodationVoteController {

    private final AccommodationVoteService accommodationVoteService;

    private final AccommodationVoteRepository accommodationVoteRepository;
}
