package com.zpi.transportservice.commons;

public class Constants {
    private Constants(){}

    public static final String ACCESS_TOKEN = "access_token";
    public static final String EXPIRATION = "expires_in";
    public static final String BASE_URL = "https://api.lufthansa.com";
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String GRANT_TYPE = "grant_type";
    public static final String NEAREST_AIRPORT = "/v1/mds-references/airports/nearest/";
    public static final String FLIGHT_SCHEDULES = "/v1/operations/schedules/";
    public static final Integer THRESHOLD_DISTANCE_DESTINATION = 100 ;
    public static final Integer THRESHOLD_DISTANCE_SOURCE = 100;
    public static final Integer THRESHOLD_DISTANCE_BETWEEN_START_AND_END_AIR = 200;
    public static final Integer FLIGHT_PROPOSAL_LIMIT = 1;
    public static final Integer COUNTRY_LIMIT = 1;
    public static final String COUNTRY_QUERY = "/v1/mds-references/countries/";
    public static final String DENMARK = "DK";






}
