package com.zpi.accommodationservice.dto;

import com.zpi.accommodationservice.accommodation.Accommodation;

import java.util.List;

public record AccommodationWithVotesDto (Accommodation accommodation, List<UserDto> userVoted){
}
