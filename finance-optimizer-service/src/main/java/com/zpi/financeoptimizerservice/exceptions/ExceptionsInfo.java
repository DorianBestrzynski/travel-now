package com.zpi.financeoptimizerservice.exceptions;

public class ExceptionsInfo {
    public static final String NOT_A_GROUP_MEMBER = "User is not part of the group";

    public final static String ILLEGAL_ARGUMENT_FEIGN = "In request there were illegal argument via feign";

    public final static String EXCEPTION_FEIGN = "There was exception while feign communication";

    public final static String ENTITY_NOT_FOUND = "There is no matching entity with given parameters";

    public final static String PERMISSION_VIOLATION = "To perform this action you need to be either coordinator or author of expenditure";

    public final static String INSUFFICIENT_PERMISSIONS = "Insufficient permissions";

    public final static String INVALID_PARAMS = "Group id or user id is invalid";


}
