package com.zpi.transportservice.aspects;


import com.zpi.transportservice.exception.ApiPermissionException;
import com.zpi.transportservice.proxy.AccommodationProxy;
import com.zpi.transportservice.proxy.TripGroupProxy;
import com.zpi.transportservice.security.CustomUsernamePasswordAuthenticationToken;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.hibernate.validator.constraints.ru.INN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;

import static com.zpi.transportservice.exception.ExceptionsInfo.INSUFFICIENT_PERMISSIONS;


@Aspect
@Component
public class AuthorizationAspect {

    @Autowired
    private AccommodationProxy accommodationProxy;

    @Autowired
    private TripGroupProxy tripGroupProxy;

    private static final String INNER_COMMUNICATION = "microserviceCommunication";


    @Around("@annotation(AuthorizeCreatorOrCoordinator)")
    public Object authorizeCreatorOrCoordinator(ProceedingJoinPoint joinPoint) throws Throwable {
        CustomUsernamePasswordAuthenticationToken authentication = (CustomUsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        var accommodation = accommodationProxy.getAccommodationInfo(INNER_COMMUNICATION, getAccommodationId(joinPoint));

        if (!(Objects.equals(accommodation.creatorId(), authentication.getUserId()) || tripGroupProxy.isUserCoordinator(INNER_COMMUNICATION, accommodation.groupId(), authentication.getUserId()))){
            throw new ApiPermissionException(INSUFFICIENT_PERMISSIONS);
        }
        return joinPoint.proceed();
    }

    private Long getAccommodationId(ProceedingJoinPoint joinPoint) {
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        return (Long) joinPoint.getArgs()[Arrays.asList(codeSignature.getParameterNames()).indexOf("accommodationId")];
    }


}
