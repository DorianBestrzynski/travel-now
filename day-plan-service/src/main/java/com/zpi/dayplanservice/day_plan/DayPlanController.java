package com.zpi.dayplanservice.day_plan;

import com.zpi.dayplanservice.dto.DayPlanDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/v1/day-plan")
@RequiredArgsConstructor
public class DayPlanController {

    private final DayPlanService dayPlanService;

    private final DayPlanRepository dayPlanRepository;

    @GetMapping()
    public ResponseEntity<List<DayPlan>> getAllDayPlansForGroup(@RequestParam Long groupId){
        var result = dayPlanService.getAllDayPlansForGroup(groupId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<DayPlan> createDayPlan(@Valid @RequestBody DayPlanDto dayPlanDto){
        var result = dayPlanService.createDayPlan(dayPlanDto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.OK)
    public void deleteDayPlan(@RequestParam(name = "dayPlanId")Long dayPlanId) {
        dayPlanService.deleteDayPlan(dayPlanId);
    }

    @PatchMapping()
    public ResponseEntity<DayPlan> editDayPlan(@RequestParam(name = "dayPlanId")Long dayPlanId,
                                               @RequestBody DayPlanDto dayPlanDto){
        var result = dayPlanService.editDayPlan(dayPlanId, dayPlanDto);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    @GetMapping("sampleData")
    public String getAllDayPlansForGroup(){
        var dayPlan1 = new DayPlan(22L, LocalDate.now(),"Test");
        dayPlanRepository.save(dayPlan1);
        return "Added sample data";
    }

    @PatchMapping("/start")
    public void setStartingPoint(@RequestParam(name = "dayPlanId") Long dayPlanId,
                                         @RequestParam(name = "attractionId") Long attractionId){
        dayPlanService.setStartingPoint(dayPlanId, attractionId);
    }

}
