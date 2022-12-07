package com.zpi.financeoptimizerservice.exceptions;

public class ExceptionsInfo {
    public static final String NOT_A_GROUP_MEMBER = "User is not part of the group";

    public final static String ILLEGAL_ARGUMENT_FEIGN = "In request there were illegal argument via feign";

    public final static String EXCEPTION_FEIGN = "There was exception while feign communication";

    public final static String ENTITY_NOT_FOUND = "There is no matching entity with given parameters";

    public final static String PERMISSION_VIOLATION = "To perform this action you need to be either coordinator or author of expenditure";

    public final static String INSUFFICIENT_PERMISSIONS = "Insufficient permissions";

    public final static String INVALID_PARAMS = "Group id or user id is invalid";

    public final static String RESOURCE_NOT_FOUND_FEIGN = "Resource was not found";

    public final static String UNPROCESSABLE_ENTITY_FEIGN = "Given entity was unprocessable";

    public final static String FORBIDDEN_FEIGN = "You do not have access to this resource or action";

    public final static String SERVICE_UNAVAILABLE_FEIGN = "Requested service is currently unavailable";

    public final static String UNAUTHORIZED_REQUEST_FEIGN = "Request was unauthorized ";



}
