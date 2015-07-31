package org.mayconbordin.oauth2.client;

/**
 *
 * @author Maycon Bordin <mayconbordin@gmail.com>
 */
public class UnauthorizedClientException extends OAuth2Exception {

    public UnauthorizedClientException(int httpCode, String response) {
        super("The client is not authorized to request an access token using this method.", httpCode, response);
    }
    
}
