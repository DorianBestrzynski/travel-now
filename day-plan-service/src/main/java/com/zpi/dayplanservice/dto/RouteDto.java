package com.zpi.dayplanservice.dto;

import com.zpi.dayplanservice.attraction.Attraction;

import java.util.List;

public record RouteDto(List<Attraction> attractions, Long distance) {
}