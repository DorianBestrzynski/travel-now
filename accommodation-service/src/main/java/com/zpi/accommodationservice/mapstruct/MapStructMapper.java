package com.zpi.accommodationservice.mapstruct;

import com.zpi.accommodationservice.accommodation.Accommodation;
import com.zpi.accommodationservice.dto.AccommodationDataDto;
import com.zpi.accommodationservice.dto.AccommodationDto;
import com.zpi.accommodationservice.dto.AccommodationVoteDto;
import com.zpi.accommodationservice.votes.AccommodationVoteId;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MapStructMapper {

    void updateFromAccommodationDtoToAccommodation(@MappingTarget Accommodation accommodation, AccommodationDto accommodationDto);

    @Mapping(target = "groupId", ignore = true)
    @Mapping(target = "creatorId", ignore = true)
    AccommodationDto adaptAccommodationDto(AccommodationDto accommodationDto);

    @Mapping(target = "sourceLink", source = "sourceLink")
    void updateFromAccommodationDataDtoToAccommodation(@MappingTarget Accommodation accommodation, AccommodationDataDto accommodationDataDto);

    AccommodationVoteId getAccommodationVoteIdFromDto(AccommodationVoteDto accommodationVoteDto);
}
