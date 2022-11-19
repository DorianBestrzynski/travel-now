package com.zpi.dayplanservice.dto;

import java.util.List;

public record RouteDto(List<AttractionPlanDto> attractions, Long distance) {
}