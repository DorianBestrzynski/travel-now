package com.zpi.dayplanservice.aspects;

import com.zpi.dayplanservice.attraction.Attraction;
import com.zpi.dayplanservice.attraction.AttractionRepository;
import com.zpi.dayplanservice.day_plan.DayPlanRepository;
import com.zpi.dayplanservice.dto.DayPlanDto;
import com.zpi.dayplanservice.exception.ApiPermissionException;
import com.zpi.dayplanservice.proxies.TripGroupProxy;
import com.zpi.dayplanservice.security.CustomUsernamePasswordAuthenticationToken;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.zpi.dayplanservice.exception.ExceptionInfo.INSUFFICIENT_PERMISSIONS;

@Aspect
@Component
public class AuthorizationAspect {

    @Autowired
    private TripGroupProxy tripGroupProxy;

    @Autowired
    private DayPlanRepository dayPlanRepository;

    @Autowired
    private AttractionRepository attractionRepository;

    private static final String INNER_COMMUNICATION = "microserviceCommunication";


    @Around("@annotation(AuthorizeCoordinator)")
    public Object authorizeCoordinator(ProceedingJoinPoint joinPoint) throws Throwable {
        CustomUsernamePasswordAuthenticationToken authentication = (CustomUsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if(!tripGroupProxy.isUserCoordinator(INNER_COMMUNICATION ,getGroupId(joinPoint), authentication.getUserId()))
            throw new ApiPermissionException(INSUFFICIENT_PERMISSIONS);


        return joinPoint.proceed();
    }

    @Around("@annotation(AuthorizePartOfTheGroup)")
    public Object authorizePartOfTheGroup(ProceedingJoinPoint joinPoint) throws Throwable {
        CustomUsernamePasswordAuthenticationToken authentication = (CustomUsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if(!tripGroupProxy.isUserPartOfTheGroup(INNER_COMMUNICATION ,getGroupId(joinPoint), authentication.getUserId()))
            throw new ApiPermissionException(INSUFFICIENT_PERMISSIONS);


        return joinPoint.proceed();
    }

    private Long getGroupId(ProceedingJoinPoint joinPoint) {
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        if(Arrays.asList(codeSignature.getParameterNames()).contains("groupId")) {
            return (Long) joinPoint.getArgs()[Arrays.asList(codeSignature.getParameterNames()).indexOf("groupId")];
        } else if(Arrays.asList(codeSignature.getParameterNames()).contains("dayPlanDto")){
            return ((DayPlanDto) joinPoint.getArgs()[Arrays.asList(codeSignature.getParameterNames()).indexOf("dayPlanDto")]).groupId();
        } else if (Arrays.asList(codeSignature.getParameterNames()).contains("dayPlanId")) {
            return dayPlanRepository.findById((Long) joinPoint.getArgs()[Arrays.asList(codeSignature.getParameterNames()).indexOf("dayPlanId")])
                                    .orElseThrow()
                                    .getGroupId();
        } else if(Arrays.asList(codeSignature.getParameterNames()).contains("attraction")){
            return attractionRepository.findById(((Attraction) joinPoint.getArgs()[Arrays.asList(codeSignature.getParameterNames()).indexOf("attraction")]).getAttractionId())
                                       .orElseThrow()
                                       .getDays()
                                       .stream()
                                       .findAny()
                                       .orElseThrow()
                                       .getGroupId();
        }

        return -1L;
    }
}
