package com.zpi.dayplanservice.day_plan;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/day-plan")
@RequiredArgsConstructor
public class DayPlanController {
    private final DayPlanService dayPlanService;
}
