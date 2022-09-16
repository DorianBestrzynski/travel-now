package com.zpi.dayplanservice.mapstruct;

import com.zpi.dayplanservice.day_plan.DayPlan;
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

    @Mapping(target = "groupId", ignore = true)
    DayPlanDto adaptDayPlanDto(DayPlanDto dayPlanDto);

}
