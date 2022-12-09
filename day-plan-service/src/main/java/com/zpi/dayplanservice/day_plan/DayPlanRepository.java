package com.zpi.dayplanservice.day_plan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface DayPlanRepository extends JpaRepository<DayPlan, Long> {

    @Query("SELECT d FROM DayPlan d WHERE d.groupId =?1 ORDER BY d.date ASC ")
    List<DayPlan> findAllByGroupId(Long groupId);

    DayPlan findDayPlanByGroupIdAndDate(Long groupId, LocalDate date);

    DayPlan findDayPlanByDate(LocalDate date);
}
