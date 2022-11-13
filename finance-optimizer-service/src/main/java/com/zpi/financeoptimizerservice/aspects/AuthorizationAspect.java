package com.zpi.financeoptimizerservice.aspects;


import com.zpi.financeoptimizerservice.exceptions.ApiPermissionException;
import com.zpi.financeoptimizerservice.expenditure.ExpenditureRepository;
import com.zpi.financeoptimizerservice.financial_request.FinancialRequestRepository;
import com.zpi.financeoptimizerservice.proxies.UserGroupProxy;
import com.zpi.financeoptimizerservice.security.CustomUsernamePasswordAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.Objects;

import static com.zpi.financeoptimizerservice.exceptions.ExceptionsInfo.*;


@Aspect
@Component
@RequiredArgsConstructor
public class AuthorizationAspect {

    private final UserGroupProxy userGroupProxy;

    private final ExpenditureRepository expenditureRepository;

    private final FinancialRequestRepository financialRequestRepository;

    private static final String INNER_COMMUNICATION = "microserviceCommunication";


    @Around("@annotation(AuthorizePartOfTheGroup)")
    public Object authorizePartOfTheGroup(ProceedingJoinPoint joinPoint) throws Throwable {
        CustomUsernamePasswordAuthenticationToken authentication = (CustomUsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        if (!userGroupProxy.isUserPartOfTheGroup(INNER_COMMUNICATION, getGroupId(joinPoint), authentication.getUserId()))
            throw new ApiPermissionException(INSUFFICIENT_PERMISSIONS);


        return joinPoint.proceed();
    }

    @Around("@annotation(AuthorizeAuthorOrCoordinatorExpenditure)")
    public Object authorizeAuthorOrCoordinatorExpenditure(ProceedingJoinPoint joinPoint) throws Throwable {
        CustomUsernamePasswordAuthenticationToken authentication = (CustomUsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        var expenditure = expenditureRepository.findById(getExpenditureId(joinPoint)).orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        if (!(userGroupProxy.isUserCoordinator(INNER_COMMUNICATION, getGroupId(joinPoint), authentication.getUserId()) || Objects.equals(expenditure.getCreatorId(), authentication.getUserId()))) {
            throw new ApiPermissionException(INSUFFICIENT_PERMISSIONS);
        }

        return joinPoint.proceed();
    }

    @Around("@annotation(AuthorizeAuthorOrCoordinatorRequest)")
    public Object authorizeAuthorOrCoordinatorRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        CustomUsernamePasswordAuthenticationToken authentication = (CustomUsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        var financialRequest = financialRequestRepository.findById(getFinancialRequestId(joinPoint)).orElseThrow();

        if (!(userGroupProxy.isUserCoordinator(INNER_COMMUNICATION, getGroupId(joinPoint), authentication.getUserId()) || financialRequest.getDebtee().equals(authentication.getUserId()))) {
            throw new ApiPermissionException(INSUFFICIENT_PERMISSIONS);
        }

        return joinPoint.proceed();
    }

    private Long getGroupId(ProceedingJoinPoint joinPoint) {
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        return (Long) joinPoint.getArgs()[Arrays.asList(codeSignature.getParameterNames()).indexOf("groupId")];
    }
    private Long getExpenditureId(ProceedingJoinPoint joinPoint) {
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        return (Long) joinPoint.getArgs()[Arrays.asList(codeSignature.getParameterNames()).indexOf("expenditureId")];
    }

    private Long getFinancialRequestId(ProceedingJoinPoint joinPoint) {
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        return (Long) joinPoint.getArgs()[Arrays.asList(codeSignature.getParameterNames()).indexOf("requestId")];
    }
}
