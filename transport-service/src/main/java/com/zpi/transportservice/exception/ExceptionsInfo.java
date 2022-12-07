package com.zpi.transportservice.exception;

public class ExceptionsInfo {
    public final static String ILLEGAL_ARGUMENT_FEIGN = "In request there were illegal argument via feign";

    public final static String EXCEPTION_FEIGN = "There was exception while feign communication";

    public final static String LUFTHANSA_API_EXCEPTION = "There was error while trying to access Lufthansa API";

    public final static String LUFTHANSA_NO_AIRPORT_MATCHING = "There are no airports matching your localization";

    public final static String INSUFFICIENT_PERMISSIONS = "Insufficient permissions";

    public final static String RESOURCE_NOT_FOUND_FEIGN = "Resource was not found";

    public final static String UNPROCESSABLE_ENTITY_FEIGN = "Given entity was unprocessable";

    public final static String FORBIDDEN_FEIGN = "You do not have access to this resource or action";

    public final static String SERVICE_UNAVAILABLE_FEIGN = "Requested service is currently unavailable";

    public final static String UNAUTHORIZED_REQUEST_FEIGN = "Request was unauthorized ";


}
