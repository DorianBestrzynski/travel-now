package com.zpi.dayplanservice.day_plan;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DayPlanRepository extends JpaRepository<DayPlan, Long> {
}
