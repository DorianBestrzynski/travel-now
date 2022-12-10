package com.zpi.dayplanservice.exception;

public final class ExceptionInfo {
    private ExceptionInfo(){}

    public final static String DAY_PLAN_NOT_FOUND = "There are no day plans for given id";

    public final static String ATTRACTION_NOT_FOUND = "There are no attraction for given id";

    public final static String INVALID_GROUP_ID = "Group id is invalid. Id must be a positive number";

    public final static String TAKEN_DATE = "There is already a day plan for this date.";

    public final static String CREATING_PERMISSION_VIOLATION = "Only user with status COORDINATOR can add new day plan!";

    public final static String DELETING_PERMISSION_VIOLATION = "Only user with status COORDINATOR can delete day plan!";

    public final static String EDITING_PERMISSION_VIOLATION = "Only user with status COORDINATOR can delete day plan!";

    public final static String DAY_PLAN_CREATION_VALIDATION_ERROR = "Error while validating day plan";

    public final static String REQUESTED_ENTITY_NOT_FOUND_FEIGN = "Requested entity not found via feign";

    public final static String UNAUTHORIZED_REQUEST_FEIGN = "Request was unauthorized via feign";

    public final static String ILLEGAL_ARGUMENT_FEIGN = "In request there were illegal argument via feign";

    public final static String EXCEPTION_FEIGN = "There was exception while feign communication";

    public final static String INVALID_USER_ID = "User id is invalid. Id must be a positive number";

    public final static String INVALID_DAY_PLAN_ID = "Day plan id is invalid. Id must be a positive number";

    public final static String NOT_A_GROUP_MEMBER = "This user is not a member of this group!";

    public final static String INSUFFICIENT_PERMISSIONS = "Insufficient permissions";

    public final static String UNPROCESSABLE_ENTITY_FEIGN = "Given entity was unprocessable";

    public final static String SERVICE_UNAVAILABLE_FEIGN = "Requested service is currently unavailable";
}
