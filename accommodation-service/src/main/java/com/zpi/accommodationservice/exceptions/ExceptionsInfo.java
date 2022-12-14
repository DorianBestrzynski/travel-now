package com.zpi.accommodationservice.exceptions;

public class ExceptionsInfo {
    public static final String NOT_A_GROUP_MEMBER = "User is not part of the group";

    public static final String PARSE_ERROR_JSON = "Error while parsing JSON in accommodation service";

    public final static String ILLEGAL_ARGUMENT_FEIGN = "In request there were illegal argument via feign";

    public final static String EXCEPTION_FEIGN = "There was exception while feign communication";

    public final static String INVALID_USER_ID = "User id is invalid. Id must be a positive number";

    public final static String INVALID_GROUP_ID = "Group id is invalid. Id must be a positive number";

    public final static String INVALID_ACCOMMODATION_ID = "Accommodation id is invalid. Id must be a positive number";

    public final static String ENTITY_NOT_FOUND = "There is no matching entity with given parameters";

    public final static String DATA_EXTRACTION_EXCEPTION = "Data extraction not supported for this service";

    public final static String DELETING_PERMISSION_VIOLATION = "Only user with status COORDINATOR or creator of accommodation can delete accommodation!";

    public final static String EDITING_PERMISSION_VIOLATION = "Only user with status COORDINATOR or creator of accommodation can edit accommodation!";

    public final static String INSUFFICIENT_PERMISSIONS = "Insufficient permissions";

    public final static String RESOURCE_NOT_FOUND_FEIGN = "Resource was not found";

    public final static String UNPROCESSABLE_ENTITY_FEIGN = "Given entity was unprocessable";

    public final static String FORBIDDEN_FEIGN = "You do not have access to this resource or action";

    public final static String SERVICE_UNAVAILABLE_FEIGN = "Requested service is currently unavailable";

    public final static String UNAUTHORIZED_REQUEST_FEIGN = "Request was unauthorized ";









}
