package com.zpi.availabilityservice.aspects;

import com.zpi.availabilityservice.dto.AvailabilityDto;
import com.zpi.availabilityservice.exceptions.ApiPermissionException;
import com.zpi.availabilityservice.proxies.TripGroupProxy;
import com.zpi.availabilityservice.security.CustomUsernamePasswordAuthenticationToken;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.zpi.availabilityservice.exceptions.ExceptionInfo.INSUFFICIENT_PERMISSIONS;


@Aspect
@Component
public class AuthorizationAspect {

    @Autowired
    private TripGroupProxy tripGroupProxy;

    private static final String INNER_COMMUNICATION = "microserviceCommunication";


    @Around("@annotation(AuthorizeCoordinator)")
    public Object authorizeCoordinator(ProceedingJoinPoint joinPoint) throws Throwable {
        CustomUsernamePasswordAuthenticationToken authentication = (CustomUsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if(!tripGroupProxy.isUserCoordinator(INNER_COMMUNICATION, getGroupId(joinPoint), authentication.getUserId()))
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

    @Around("@annotation(AuthorizeAuthor)")
    public Object authorizeAuthor(ProceedingJoinPoint joinPoint) throws Throwable {
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
            return ((AvailabilityDto) joinPoint.getArgs()[Arrays.asList(codeSignature.getParameterNames()).indexOf("availabilityDto")]).groupId();
        }
    }
}
