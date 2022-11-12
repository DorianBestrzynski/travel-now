package com.zpi.accommodationservice.accommodationservice.exceptions;

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








}
