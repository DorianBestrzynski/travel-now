package com.zpi.accommodationservice.accommodationservice.mapstruct;

import com.zpi.accommodationservice.accommodationservice.accommodation.Accommodation;
import com.zpi.accommodationservice.accommodationservice.dto.AccommodationDataDto;
import com.zpi.accommodationservice.accommodationservice.dto.AccommodationDto;
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

    void updateFromAccommodationDataDtoToAccommodation(@MappingTarget Accommodation accommodation, AccommodationDataDto accommodationDataDto);
}
