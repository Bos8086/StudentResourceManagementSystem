package com.ecobank.srms.security;

public class SecurityConstants {
	    public static final String SECRET = "SecretKeyToGenJWTsForAfricaCollect";
	    public static final String ISSUER = "Anything";
	    public static final long EXPIRATION_TIME = 3_600_000; // 1hr
	    public static final String TOKEN_PREFIX = "Bearer ";
	    public static final String HEADER_STRING = "Authorization";
	    public static final String GET_AUTH_TOKEN = "/api/v1/token";
	    public static final String LOGIN = "/api/v1/user/login";
	   
	

}
