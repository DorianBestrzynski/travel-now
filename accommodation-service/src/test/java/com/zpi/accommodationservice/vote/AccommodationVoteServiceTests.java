package com.zpi.accommodationservice.vote;

import com.zpi.accommodationservice.accommodation.Accommodation;
import com.zpi.accommodationservice.accommodation.AccommodationRepository;
import com.zpi.accommodationservice.dto.AccommodationVoteDto;
import com.zpi.accommodationservice.proxies.UserGroupProxy;
import com.zpi.accommodationservice.security.CustomUsernamePasswordAuthenticationToken;
import com.zpi.accommodationservice.votes.AccommodationVote;
import com.zpi.accommodationservice.votes.AccommodationVoteId;
import com.zpi.accommodationservice.votes.AccommodationVoteRepository;
import com.zpi.accommodationservice.votes.AccommodationVoteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class AccommodationVoteServiceTests {
    @MockBean
    AccommodationRepository accommodationRepository;

    @MockBean
    AccommodationVoteRepository accommodationVoteRepository;

    @Autowired
    @InjectMocks
    AccommodationVoteService accommodationVoteService;

    @MockBean
    UserGroupProxy userGroupProxy;

    void mockAuthorizePartOfTheGroupAspect(){
        Authentication authentication = new CustomUsernamePasswordAuthenticationToken(null, null, 1L);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        doReturn(Boolean.TRUE).when(userGroupProxy).isUserPartOfTheGroup(any(), any(), any());
    }

    void mockAuthorizeAuthorOrCoordinatorExpenditureAspect(){
        Authentication authentication = new CustomUsernamePasswordAuthenticationToken(null, null, 1L);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(accommodationRepository.findById(any())).thenReturn(Optional.of(new Accommodation()));
        doReturn(Boolean.TRUE).when(userGroupProxy).isUserPartOfTheGroup(any(), any(), any());
        doReturn(Boolean.TRUE).when(userGroupProxy).isUserCoordinator(any(), any(), any());
    }

    @Test
    void shouldVoteForAccommodation() {
        //given
        mockAuthorizePartOfTheGroupAspect();
        var accommodationVoteDto = new AccommodationVoteDto(1L, 1L, 1L);
        var accommodation = new Accommodation();
        accommodation.setGivenVotes(0);

        //given
        when(accommodationVoteRepository.existsById(any())).thenReturn(false);
        when(accommodationRepository.existsByAccommodationIdAndGroupId(anyLong(), anyLong())).thenReturn(true);
        when(accommodationRepository.findById(anyLong())).thenReturn(Optional.of(accommodation));
        accommodationVoteService.vote(accommodationVoteDto);

        //then
        assertEquals(1, accommodation.getGivenVotes());
        verify(accommodationVoteRepository).save(any(AccommodationVote.class));
    }

    @Test
    void shouldDeleteVote() {
        //given
        mockAuthorizePartOfTheGroupAspect();
        var accommodationVoteId = new AccommodationVoteId(1L, 1L);
        var accommodationVoteDto = new AccommodationVoteDto(1L, 1L, 1L);
        var accommodation = new Accommodation();
        accommodation.setGivenVotes(1);

        //given
        when(accommodationVoteRepository.findById(any())).thenReturn(Optional.of(new AccommodationVote(accommodationVoteId)));
        when(accommodationRepository.findById(anyLong())).thenReturn(Optional.of(accommodation));
        accommodationVoteService.deleteVote(accommodationVoteId);

        //then
        assertEquals(0, accommodation.getGivenVotes());
        verify(accommodationVoteRepository).delete(any(AccommodationVote.class));
    }

    @Test
    void shouldReturnVotesForAccommodation() {
        //when
        when(accommodationVoteRepository.findAllByIdAccommodationId(anyLong())).thenReturn(new ArrayList<>());
        accommodationVoteService.getVotesForAccommodation(1L);

        //then
        verify(accommodationVoteRepository).findAllByIdAccommodationId(anyLong());
    }

    @Test
    void shouldThrowExceptionsWhenAlreadyVoted() {
        //given
        mockAuthorizePartOfTheGroupAspect();
        var accommodationVoteDto = new AccommodationVoteDto(1L, 1L, 1L);

        //when
        when(accommodationVoteRepository.existsById(any(AccommodationVoteId.class))).thenReturn(true);

        //then
        assertThrows(IllegalArgumentException.class, () -> accommodationVoteService.vote(accommodationVoteDto));
    }

    @Test
    void shouldThrowExceptionsWhenInvalidAccommodation() {
        //given
        mockAuthorizePartOfTheGroupAspect();
        var accommodationVoteDto = new AccommodationVoteDto(1L, 1L, 1L);

        //when
        when(accommodationVoteRepository.existsById(any(AccommodationVoteId.class))).thenReturn(false);
        when(accommodationRepository.existsByAccommodationIdAndGroupId(anyLong(), anyLong())).thenReturn(false);

        //then
        assertThrows(IllegalArgumentException.class, () -> accommodationVoteService.vote(accommodationVoteDto));
    }
}
