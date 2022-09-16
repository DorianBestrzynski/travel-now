package com.zpi.dayplanservice.day_plan;
import com.zpi.dayplanservice.dto.DayPlanDto;
import com.zpi.dayplanservice.exception.ApiPermissionException;
import com.zpi.dayplanservice.exception.ApiRequestException;
import com.zpi.dayplanservice.exception.IllegalDateException;
import com.zpi.dayplanservice.mapstruct.MapStructMapper;
import com.zpi.dayplanservice.proxies.TripGroupProxy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;
import static com.zpi.dayplanservice.exception.ExceptionInfo.*;

@Service
@RequiredArgsConstructor
public class DayPlanService {

    private final DayPlanRepository dayPlanRepository;

    private final TripGroupProxy tripGroupProxy;

    private final MapStructMapper mapstructMapper;


    public List<DayPlan> getAllDayPlansForGroup(Long groupId) {
        if(groupId == null){
            throw new IllegalArgumentException(INVALID_GROUP_ID);
        }
        var dayPlans = dayPlanRepository.findAllByGroupId(groupId);
        if(dayPlans.isEmpty()) throw new ApiRequestException(DAY_PLAN_NOT_FOUND);
        return dayPlans;

    }
    @Transactional
    public DayPlan createDayPlan(Long userId, DayPlanDto dayPlanDto) {
        if(tripGroupProxy.isUserCoordinator(dayPlanDto.groupId(),userId)){
            if(isDateAvailable(dayPlanDto)){
                var dayPlan = new DayPlan(dayPlanDto.groupId(), dayPlanDto.date() ,dayPlanDto.name());
                dayPlanRepository.save(dayPlan);
                return dayPlan;
            }
            throw new IllegalDateException(TAKEN_DATE);
        }
        throw new ApiPermissionException(CREATING_PERMISSION_VIOLATION);

    }

    private boolean isDateAvailable(DayPlanDto dayPlanDto) {
        return dayPlanRepository.findDayPlanByGroupIdAndDate(dayPlanDto.groupId(), dayPlanDto.date()) == null;
    }

    @Transactional
    public void deleteDayPlan(Long dayPlanId, Long userId) {
        if(dayPlanId == null || userId == null ){
            throw new IllegalArgumentException(INVALID_DAY_PLAN_ID + "or" + INVALID_USER_ID);
        }
        var dayPlan = dayPlanRepository.findById(dayPlanId).orElseThrow(() -> new ApiRequestException(DAY_PLAN_NOT_FOUND));
        if(tripGroupProxy.isUserCoordinator(dayPlan.getGroupId() ,userId)){
            dayPlanRepository.deleteById(dayPlanId);
        }
        else throw new ApiPermissionException(DELETING_PERMISSION_VIOLATION);


    }

    @Transactional
    public DayPlan editDayPlan(Long dayPlanId, Long userId, DayPlanDto dayPlanDto) {
        if(dayPlanId == null || userId == null ){
            throw new IllegalArgumentException(INVALID_DAY_PLAN_ID + "or" + INVALID_USER_ID);
        }
        var dayPlan = dayPlanRepository.findById(dayPlanId).orElseThrow(() -> new ApiRequestException(DAY_PLAN_NOT_FOUND));

        if(tripGroupProxy.isUserCoordinator(dayPlan.getGroupId() ,userId)){
            dayPlanDto = mapstructMapper.adaptDayPlanDto(dayPlanDto);
            mapstructMapper.updateFromDayPlanDtoToDayPlan(dayPlan, dayPlanDto);
            dayPlanRepository.save(dayPlan);
            return dayPlan;
        }

        throw new ApiPermissionException(EDITING_PERMISSION_VIOLATION);

    }
}
