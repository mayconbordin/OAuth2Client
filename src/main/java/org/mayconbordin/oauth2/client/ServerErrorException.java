package org.mayconbordin.oauth2.client;

/**
 *
 * @author Maycon Bordin <mayconbordin@gmail.com>
 */
public class ServerErrorException extends OAuth2Exception {

    public ServerErrorException() {
        super("An error occurred on the authorization server.");
    }
    
}
