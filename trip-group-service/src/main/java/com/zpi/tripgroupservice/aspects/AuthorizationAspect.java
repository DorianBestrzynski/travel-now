package com.zpi.tripgroupservice.aspects;

import com.zpi.tripgroupservice.exception.ExceptionInfo;
import com.zpi.tripgroupservice.exception.ApiPermissionException;
import com.zpi.tripgroupservice.security.CustomUsernamePasswordAuthenticationToken;
import com.zpi.tripgroupservice.user_group.UserGroupService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class AuthorizationAspect {

    @Autowired
    private UserGroupService userGroupService;

    @Around("@annotation(com.zpi.tripgroupservice.aspects.AuthorizeCoordinator)")
    public Object authorizeCoordinator(ProceedingJoinPoint joinPoint) throws Throwable {
        CustomUsernamePasswordAuthenticationToken authentication = (CustomUsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
       if(!userGroupService.isUserCoordinator(authentication.getUserId(), getGroupId(joinPoint)))
            throw new ApiPermissionException(ExceptionInfo.INSUFFICIENT_PERMISSIONS);

        return joinPoint.proceed();
    }

    @Around("@annotation(com.zpi.tripgroupservice.aspects.AuthorizePartOfTheGroup)")
    public Object authorizePartOfTheGroup(ProceedingJoinPoint joinPoint) throws Throwable {
        CustomUsernamePasswordAuthenticationToken authentication = (CustomUsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if(!userGroupService.checkIfUserIsInGroup(authentication.getUserId(), getGroupId(joinPoint)))
            throw new ApiPermissionException(ExceptionInfo.INSUFFICIENT_PERMISSIONS);

        return joinPoint.proceed();
    }

    private Long getGroupId(ProceedingJoinPoint joinPoint) {
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        return (Long) joinPoint.getArgs()[Arrays.asList(codeSignature.getParameterNames()).indexOf("groupId")];
    }
}
