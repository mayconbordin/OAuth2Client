package org.mayconbordin.oauth2.client;

/**
 * 
 * @author Maycon Bordin <mayconbordin@gmail.com>
 */
public interface OAuth2Constants {
    String ACCESS_TOKEN = "access_token";
    String CLIENT_ID = "client_id";
    String CLIENT_SECRET = "client_secret";
    String REFRESH_TOKEN = "refresh_token";
    String USERNAME = "username";
    String PASSWORD = "password";
    String AUTHENTICATION_SERVER_URL = "authentication_server_url";
    String RESOURCE_SERVER_URL = "resource_server_url";
    String GRANT_TYPE = "grant_type";
    String SCOPE = "scope";
    
    String AUTHORIZATION = "Authorization";
    String BEARER = "Bearer";
    String BASIC = "Basic";
    
    String GRANT_PASSWORD = "password";
    String GRANT_CLIENT_CREDENTIALS = "client_credentials";
    String GRANT_REFRESH_TOKEN = "refresh_token";
    
    String JSON_CONTENT = "application/json";
    String XML_CONTENT = "application/xml";
    String URL_ENCODED_CONTENT = "application/x-www-form-urlencoded";
    
    String EXPIRES_IN = "expires_in";
    String TOKEN_TYPE = "token_type";
    
    int HTTP_OK = 200;
    int HTTP_FORBIDDEN = 403;
    int HTTP_UNAUTHORIZED = 401;
}
