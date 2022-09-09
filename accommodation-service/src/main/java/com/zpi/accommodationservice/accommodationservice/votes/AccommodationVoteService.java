package com.zpi.accommodationservice.accommodationservice.votes;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccommodationVoteService {

    private final AccommodationVoteRepository accommodationVoteRepository;
}
