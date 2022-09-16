package com.zpi.dayplanservice.day_plan;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface DayPlanRepository extends JpaRepository<DayPlan, Long> {

    List<DayPlan> findAllByGroupId(Long groupId);

    DayPlan findDayPlanByGroupIdAndDate(Long groupId, LocalDate date);
}
