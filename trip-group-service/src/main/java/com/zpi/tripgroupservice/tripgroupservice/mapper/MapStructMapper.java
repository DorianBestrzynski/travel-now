package com.zpi.tripgroupservice.tripgroupservice.mapper;

import com.zpi.tripgroupservice.tripgroupservice.dto.TripGroupDto;
import com.zpi.tripgroupservice.tripgroupservice.trip_group.TripGroup;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MapStructMapper {
    @Mapping(target = "minimalNumberOfDays", ignore = true)
    @Mapping(target = "minimalNumberOfParticipants", ignore = true)
    void updateFromTripGroupDtoToTripGroup(@MappingTarget TripGroup tripGroup, TripGroupDto tripGroupDto);
}
