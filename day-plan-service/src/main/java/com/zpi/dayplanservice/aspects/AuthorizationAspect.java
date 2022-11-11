package com.zpi.dayplanservice.aspects;

import com.zpi.dayplanservice.dto.DayPlanDto;
import com.zpi.dayplanservice.exception.ApiPermissionException;
import com.zpi.dayplanservice.proxies.TripGroupProxy;
import com.zpi.dayplanservice.security.CustomUsernamePasswordAuthenticationToken;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.hibernate.validator.constraints.ru.INN;
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
        } else {
            return ((DayPlanDto) joinPoint.getArgs()[Arrays.asList(codeSignature.getParameterNames()).indexOf("dayPlanDto")]).groupId();
        }
    }
}
