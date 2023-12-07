package com.prog2final.procesador.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";

    public static final String SYSTEM = "system";
    public static final String DEFAULT_LANGUAGE = "es";

    public static final String COMP_SERVICES_URL = "http://192.168.194.254:8000/api";
    public static final String GENERATOR_URL = "http://192.168.194.135:8080/api";

    //public static final String GENERATOR_URL = "http://192.168.194.254:8000/api";

    private Constants() {}
}
