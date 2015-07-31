package org.mayconbordin.oauth2.client;

/**
 * The base exception class.
 * 
 * @author Maycon Bordin <mayconbordin@gmail.com>
 */
public class OAuth2Exception extends Exception {
    protected int httpCode;
    protected String response;
    
    public OAuth2Exception() {
        super();
    }
    
    public OAuth2Exception(String message) {
        super(message);
    }
    
    public OAuth2Exception(String message, int httpCode, String response) {
        super(message);
        
        this.httpCode = httpCode;
        this.response = response;
    }

    public OAuth2Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public OAuth2Exception(Throwable cause) {
        super(cause);
    }

    /**
     * Create a new exception based on the HTTP response code.
     * 
     * @param code The HTTP response code.
     * @param response The response content.
     * @return 
     */
    public static OAuth2Exception fromHttpCode(int code, String response) {
        if (code == 400) {
            return new InvalidRequestException(code, response);
        } else if (code == 401) {
            return new UnauthorizedClientException(code, response);
        } else if (code == 404) {
            return new ResourceNotFoundException(code, response);
        } else if (code == 500) {
            return new ServerErrorException();
        } else {
            return new OAuth2Exception("", code, response);
        }
    }

    public int getHttpCode() {
        return httpCode;
    }

    public String getResponse() {
        return response;
    }
}
