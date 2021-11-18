package com.lblocki.privatecommunicatorserver.security.utils;

public class SecurityUtils {

    public static final String BASE_HTTP_PATH = "/api/rest/v1/";

    public static final String REGISTRATION_HTTP_PATH = BASE_HTTP_PATH + "auth/register";

    public static final String HTTP_UPGRADE_PATH = BASE_HTTP_PATH + "ws/**";    //todo make it more specific

    public static final String USERNAME_HEADER = "username";
    public static final String PASSWORD_HEADER = "password";

}
