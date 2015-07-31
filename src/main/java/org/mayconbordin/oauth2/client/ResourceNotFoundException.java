package org.mayconbordin.oauth2.client;

/**
 *
 * @author Maycon Bordin <mayconbordin@gmail.com>
 */
public class ResourceNotFoundException extends OAuth2Exception {
    public ResourceNotFoundException(int httpCode, String response) {
        super("The request URI does not exists.", httpCode, response);
    }
}
