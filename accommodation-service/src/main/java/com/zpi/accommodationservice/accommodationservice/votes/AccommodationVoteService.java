package com.zpi.accommodationservice.accommodationservice.votes;

import com.zpi.accommodationservice.accommodationservice.accommodation.AccommodationRepository;
import com.zpi.accommodationservice.accommodationservice.dto.AccommodationVoteDto;
import com.zpi.accommodationservice.accommodationservice.mapstruct.MapStructMapper;
import com.zpi.accommodationservice.accommodationservice.proxies.UserGroupProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccommodationVoteService {

    private final AccommodationVoteRepository accommodationVoteRepository;

    private final MapStructMapper mapper;

    private final UserGroupProxy userGroupProxy;

    private final AccommodationRepository accommodationRepository;

    @Transactional
    public AccommodationVote vote(AccommodationVoteDto accommodationVoteDto) {
        var accommodationVoteId = mapper.getAccommodationVoteIdFromDto(accommodationVoteDto);

        if(accommodationVoteRepository.existsById(accommodationVoteId))
            throw new IllegalArgumentException("User already voted for this accommodation");
        if(!userGroupProxy.isUserPartOfTheGroup(accommodationVoteDto.groupId(), accommodationVoteDto.userId()))
            throw new IllegalArgumentException("User is not part of the group");
        if(!accommodationRepository.existsByAccommodationIdAndGroupId(accommodationVoteDto.accommodationId(), accommodationVoteDto.groupId()))
            throw new IllegalArgumentException("Accommodation does not exist in the group");

        var accommodation = accommodationRepository.findById(accommodationVoteDto.accommodationId()).orElseThrow();
        accommodation.setGivenVotes(accommodation.getGivenVotes() + 1);

        return accommodationVoteRepository.save(new AccommodationVote(accommodationVoteId));
    }

    public List<AccommodationVote> getVotesForAccommodation(Long accommodationId) {
        return accommodationVoteRepository.findAllByIdAccommodationId(accommodationId);
    }

    @Transactional
    public AccommodationVote deleteVote(AccommodationVoteId accommodationVoteId) {
        var accommodationVote = accommodationVoteRepository.findById(accommodationVoteId)
                .orElseThrow(() -> new IllegalArgumentException("User didn't vote for this accommodation"));
        var accommodation = accommodationRepository.findById(accommodationVoteId.getAccommodationId()).orElseThrow();
        accommodation.setGivenVotes(accommodation.getGivenVotes() - 1);
        accommodationVoteRepository.delete(accommodationVote);
        return accommodationVote;
    }
}
