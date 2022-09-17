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
    public ResponseEntity<List<DayPlan>> getAllDayPlansForGroup(@RequestParam Long groupId, @RequestParam Long userId){
        var result = dayPlanService.getAllDayPlansForGroup(groupId, userId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<DayPlan> createDayPlan(@PathVariable Long userId, @Valid @RequestBody DayPlanDto dayPlanDto){
        var result = dayPlanService.createDayPlan(userId, dayPlanDto);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteDayPlan(@RequestParam(name = "dayPlanId")Long dayPlanId, @RequestParam(name = "userId")Long userId) {
        dayPlanService.deleteDayPlan(dayPlanId, userId);
    }

    @PatchMapping()
    public ResponseEntity<DayPlan> editDayPlan(@RequestParam(name = "dayPlanId")Long dayPlanId,
                                               @RequestParam(name = "userId")Long userId,
                                               @RequestBody DayPlanDto dayPlanDto){
        var result = dayPlanService.editDayPlan(dayPlanId, userId, dayPlanDto);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    @GetMapping("sampleData")
    public String getAllDayPlansForGroup(){
        var dayPlan1 = new DayPlan(1L, LocalDate.now(),"Test");
        dayPlanRepository.save(dayPlan1);
        return "Added sample data";
    }
}
