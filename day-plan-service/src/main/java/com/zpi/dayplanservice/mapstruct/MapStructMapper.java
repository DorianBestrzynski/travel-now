package com.zpi.dayplanservice.mapstruct;

import com.zpi.dayplanservice.attraction.Attraction;
import com.zpi.dayplanservice.day_plan.DayPlan;
import com.zpi.dayplanservice.dto.AttractionCandidateDto;
import com.zpi.dayplanservice.dto.DayPlanDto;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MapStructMapper {

    void updateFromDayPlanDtoToDayPlan(@MappingTarget DayPlan dayPlan, DayPlanDto dayPlanDto);
    @Mapping(target = "openingHours", expression = "java(java.util.Arrays.toString(attractionCandidateDto.getOpeningHours()))")
    @Mapping(target = "name", source = "attractionName")
    @Mapping(target = "attractionLink", source = "url")
    Attraction getAttractionFromCandidateDto(AttractionCandidateDto attractionCandidateDto);

    @Mapping(target = "openingHours", expression = "java(java.util.Arrays.toString(attractionCandidateDto.getOpeningHours()))")
    @Mapping(target = "name", source = "attractionName")
    @Mapping(target = "attractionLink", source = "url")
    void updateAttractionFromCandidateDto(@MappingTarget Attraction attraction,  AttractionCandidateDto attractionCandidateDto);

    @Mapping(target = "groupId", ignore = true)
    DayPlanDto adaptDayPlanDto(DayPlanDto dayPlanDto);
}
