package com.zpi.dayplanservice.day_plan;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DayPlanService {
    private final DayPlanRepository dayPlanRepository;
}
