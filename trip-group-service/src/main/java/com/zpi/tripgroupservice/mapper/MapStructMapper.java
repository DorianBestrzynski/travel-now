package com.zpi.tripgroupservice.mapper;

import com.zpi.tripgroupservice.trip_group.TripGroup;
import com.zpi.tripgroupservice.dto.TripGroupDto;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MapStructMapper {

    @Mapping(target = "tripGroup.minimalNumberOfDays", source = "minimalNumberOfDays")
    @Mapping(target = "tripGroup.minimalNumberOfParticipants", source = "minimalNumberOfParticipants")
    void updateFromTripGroupDtoToTripGroup(@MappingTarget TripGroup tripGroup, TripGroupDto tripGroupDto);
}
