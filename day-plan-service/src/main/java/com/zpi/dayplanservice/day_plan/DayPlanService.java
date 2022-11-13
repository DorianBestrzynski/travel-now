package com.zpi.dayplanservice.day_plan;

import com.zpi.dayplanservice.aspects.AuthorizeCoordinator;
import com.zpi.dayplanservice.aspects.AuthorizePartOfTheGroup;
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

    @AuthorizePartOfTheGroup
    public List<DayPlan> getAllDayPlansForGroup(Long groupId) {
        if (groupId == null) {
            throw new IllegalArgumentException(INVALID_GROUP_ID);
        }

        return dayPlanRepository.findAllByGroupId(groupId);
    }

    @Transactional
    @AuthorizeCoordinator
    public DayPlan createDayPlan(DayPlanDto dayPlanDto) {
        if (isDateAvailable(dayPlanDto)) {
            var dayPlan = new DayPlan(dayPlanDto.groupId(), dayPlanDto.date(), dayPlanDto.name());
            dayPlanRepository.save(dayPlan);
            return dayPlan;
        }
        throw new IllegalDateException(TAKEN_DATE);
    }

    private boolean isDateAvailable(DayPlanDto dayPlanDto) {
        return dayPlanRepository.findDayPlanByGroupIdAndDate(dayPlanDto.groupId(), dayPlanDto.date()) == null;
    }

    @Transactional
    @AuthorizeCoordinator
    public void deleteDayPlan(Long dayPlanId) {
        if (dayPlanId == null || dayPlanId < 0) {
            throw new IllegalArgumentException(INVALID_DAY_PLAN_ID);
        }

        dayPlanRepository.deleteById(dayPlanId);
    }

    @Transactional
    @AuthorizeCoordinator
    public DayPlan editDayPlan(Long dayPlanId, DayPlanDto dayPlanDto) {
        if (dayPlanId == null || dayPlanId < 0) {
            throw new IllegalArgumentException(INVALID_DAY_PLAN_ID);
        }
        var dayPlan = dayPlanRepository.findById(dayPlanId)
                                       .orElseThrow(() -> new ApiRequestException(DAY_PLAN_NOT_FOUND));


        dayPlanDto = mapstructMapper.adaptDayPlanDto(dayPlanDto);
        mapstructMapper.updateFromDayPlanDtoToDayPlan(dayPlan, dayPlanDto);
        dayPlanRepository.save(dayPlan);
        return dayPlan;
    }

    public List<DayPlan> getDayPlanById(List<Long> dayPlanIds, Long userId) {
        if (dayPlanIds == null || userId == null) {
            throw new IllegalArgumentException(INVALID_DAY_PLAN_ID + "or" + INVALID_USER_ID);
        }

        for (Long dayPlanId : dayPlanIds) {
            var dayPlan = dayPlanRepository.findById(dayPlanId)
                                           .orElseThrow(() -> new ApiRequestException(DAY_PLAN_NOT_FOUND));
            if (!tripGroupProxy.isUserPartOfTheGroup("innerCommunication", dayPlan.getGroupId(), userId)) {
                throw new ApiPermissionException(NOT_A_GROUP_MEMBER);
            }
        }

        return dayPlanRepository.findAllById(dayPlanIds);
    }

    public DayPlan getDayPlanById(Long dayPlanId) {
        if (dayPlanId == null) {
            throw new IllegalArgumentException(INVALID_DAY_PLAN_ID + "or" + INVALID_USER_ID);
        }

        return dayPlanRepository.findById(dayPlanId)
                                .orElseThrow(() -> new ApiRequestException(DAY_PLAN_NOT_FOUND));
    }


    public boolean doesDayPlanExist(Long dayPlanId) {
        return dayPlanRepository.existsById(dayPlanId);
    }

}
