package com.zpi.tripgroupservice.tripgroupservice.exception;

public final class ExceptionInfo {
    private ExceptionInfo(){}
    public final static String NO_GROUPS_FOR_USER = "There are no groups for requested user";
    public final static String GROUP_DOES_NOT_EXIST = "There are no group for given id: ";
    public final static String INVALID_USER_ID = "User id is invalid. Id must be a positive number";
    public final static String INVALID_GROUP_ID = "User id is invalid. Id must be a positive number";
    public final static String GROUP_CREATION_VALIDATION_ERROR = "Error while validating group";
    public final static String NEGATIVE_USER_ID_GROUP_ID = "User id or group id cannot be negative";
    public final static String USER_NOT_A_MEMBER = "User is not a member of the group";
    public final static String USER_NOT_A_COORDINATOR = "User is not a coordinator of the group";
    public final static String INVALID_INVITATION_TOKEN = "Invalid invitation token";
    public final static String USER_ALREADY_MEMBER = "User is already a member of the group";
}
