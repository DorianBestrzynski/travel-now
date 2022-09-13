package com.zpi.tripgroupservice.tripgroupservice.exception;

public final class ExceptionInfo {

    private ExceptionInfo(){}

    public final static String NO_GROUPS_FOR_USER = "There are no groups for requested user ";

    public final static String INVALID_USER_ID = "User id is invalid. Id must be a positive number ";

    public final static String INVALID_GROUP_ID = "Group id is invalid. Id must be a positive number ";

    public final static String GROUP_CREATION_VALIDATION_ERROR = "Error while validating group ";

    public final static String USER_GROUP_ENTITY_NOT_FOUND = "No user group entity matching given userId or groupId was found ";

    public final static String DELETING_PERMISSION_VIOLATION = "Only user with role of coordinator can delete group! ";

    public final static String EDITING_PERMISSION_VIOLATION = "Only user with role of coordinator can edit group! ";

    public final static String GROUP_NOT_FOUND = "There is no group with given group_id ";



}
