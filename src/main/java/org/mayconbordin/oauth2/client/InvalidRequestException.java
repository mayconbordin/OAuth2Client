package org.mayconbordin.oauth2.client;

/**
 *
 * @author Maycon Bordin <mayconbordin@gmail.com>
 */
public class InvalidRequestException extends OAuth2Exception {

    public InvalidRequestException(int httpCode, String response) {
        super("The request is not valid, probably a missing or malformed parameter.", httpCode, response);
    }
    
}
