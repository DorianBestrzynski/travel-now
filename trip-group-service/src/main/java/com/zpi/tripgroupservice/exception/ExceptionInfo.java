package com.zpi.tripgroupservice.exception;

public final class ExceptionInfo {

    private ExceptionInfo(){}
    public static final String LAST_COORDINATOR = "You cannot leave the group because you are last coordinator in group";

    public final static String NO_GROUPS_FOR_USER = "There are no groups for requested user";

    public final static String GROUP_DOES_NOT_EXIST = "There are no group for given id: ";

    public final static String INVALID_USER_ID = "User id is invalid. Id must be a positive number";

    public final static String INVALID_GROUP_ID = "Group id is invalid. Id must be a positive number";

    public final static String GROUP_CREATION_VALIDATION_ERROR = "Error while validating group";

    public final static String INVALID_USER_ID_GROUP_ID = "User id and group id must be positive";

    public final static String USER_NOT_A_MEMBER = "User is not a member of the group";

    public final static String USER_NOT_A_COORDINATOR = "User is not a coordinator of the group";

    public final static String INVALID_INVITATION_TOKEN = "Invalid invitation token";

    public final static String USER_ALREADY_MEMBER = "User is already a member of the group";

    public final static String USER_GROUP_ENTITY_NOT_FOUND = "No user group entity matching given userId or groupId was found ";

    public final static String DELETING_PERMISSION_VIOLATION = "Only user with role of coordinator can delete group! ";

    public final static String EDITING_PERMISSION_VIOLATION = "Only user with role of coordinator can edit group! ";

    public final static String GROUP_NOT_FOUND = "There is no group with given group_id ";

    public final static String USER_GROUP_NOT_FOUND = "There is no user group with given id ";

    public final static String ILLEGAL_ARGUMENT_FEIGN = "In request there were illegal argument via feign";

    public final static String EXCEPTION_FEIGN = "There was exception while feign communication";

    public final static String INVALID_ACCOMMODATION_ID = "Invalid accommodation id";

    public final static String ACCOMMODATION_NOT_FOUND = "Accommodation not found";

    public final static String CANNOT_LEAVE_GROUP = "You cannot leave group if you have unsettled expenses";

    public final static String INSUFFICIENT_PERMISSIONS = "Insufficient permissions";

    public final static String RESOURCE_NOT_FOUND_FEIGN = "Resource was not found";

    public final static String UNPROCESSABLE_ENTITY_FEIGN = "Given entity was unprocessable";

    public final static String FORBIDDEN_FEIGN = "You do not have access to this resource or action";

    public final static String SERVICE_UNAVAILABLE_FEIGN = "Requested service is currently unavailable";

    public final static String UNAUTHORIZED_REQUEST_FEIGN = "Request was unauthorized ";


}
