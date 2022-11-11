package com.zpi.accommodationservice.accommodationservice.aspects;


import com.zpi.accommodationservice.accommodationservice.accommodation.AccommodationRepository;
import com.zpi.accommodationservice.accommodationservice.dto.AccommodationDto;
import com.zpi.accommodationservice.accommodationservice.dto.AccommodationInfoDto;
import com.zpi.accommodationservice.accommodationservice.exceptions.ApiPermissionException;
import com.zpi.accommodationservice.accommodationservice.proxies.UserGroupProxy;
import com.zpi.accommodationservice.accommodationservice.security.CustomUsernamePasswordAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;

import static com.zpi.accommodationservice.accommodationservice.exceptions.ExceptionsInfo.ENTITY_NOT_FOUND;
import static com.zpi.accommodationservice.accommodationservice.exceptions.ExceptionsInfo.INSUFFICIENT_PERMISSIONS;


@Aspect
@Component
@RequiredArgsConstructor
public class AuthorizationAspect {

    private final UserGroupProxy userGroupProxy;

    private final AccommodationRepository accommodationRepository;


    private static final String INNER_COMMUNICATION = "microserviceCommunication";


    @Around("@annotation(AuthorizeCoordinator) && args(@RequestBody body)")
    public Object authorizeCoordinator(ProceedingJoinPoint joinPoint, final AccommodationDto body) throws Throwable {
        CustomUsernamePasswordAuthenticationToken authentication = (CustomUsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (!userGroupProxy.isUserCoordinator(INNER_COMMUNICATION, body.groupId(), authentication.getUserId()))
            throw new ApiPermissionException(INSUFFICIENT_PERMISSIONS);


        return joinPoint.proceed();
    }

    @Around("@annotation(AuthorizePartOfTheGroup) && args(@RequestBody body)")
    public Object authorizePartOfTheGroup(ProceedingJoinPoint joinPoint, final AccommodationDto body) throws Throwable {
        CustomUsernamePasswordAuthenticationToken authentication = (CustomUsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (!userGroupProxy.isUserPartOfTheGroup(INNER_COMMUNICATION, body.groupId(), authentication.getUserId()))
            throw new ApiPermissionException(INSUFFICIENT_PERMISSIONS);


        return joinPoint.proceed();
    }


    @Around("@annotation(AuthorizeAuthorOrCoordinator)")
    public Object authorizeAuthorOrCoordinator(ProceedingJoinPoint joinPoint) throws Throwable {
        CustomUsernamePasswordAuthenticationToken authentication = (CustomUsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        var accommodation = accommodationRepository.findById(getAccommodationId(joinPoint)).orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        if (!(userGroupProxy.isUserPartOfTheGroup(INNER_COMMUNICATION, accommodation.getGroupId(), authentication.getUserId()) &&
                (userGroupProxy.isUserCoordinator(INNER_COMMUNICATION, accommodation.getGroupId(), authentication.getUserId()) || authentication.getUserId().equals(accommodation.getCreator_id())))){
            throw new ApiPermissionException(INSUFFICIENT_PERMISSIONS);

        }
        return joinPoint.proceed();
    }

    private Long getAccommodationId(ProceedingJoinPoint joinPoint) {
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        return (Long) joinPoint.getArgs()[Arrays.asList(codeSignature.getParameterNames()).indexOf("accommodationId")];
    }

}
