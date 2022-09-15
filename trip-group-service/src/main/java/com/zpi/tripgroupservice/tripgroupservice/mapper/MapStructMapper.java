package com.zpi.tripgroupservice.tripgroupservice.mapper;

import com.zpi.tripgroupservice.tripgroupservice.dto.TripGroupDto;
import com.zpi.tripgroupservice.tripgroupservice.trip_group.TripGroup;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MapStructMapper {

    void updateFromTripGroupDtoToTripGroup(@MappingTarget TripGroup tripGroup, TripGroupDto tripGroupDto);
}
