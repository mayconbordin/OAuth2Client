package org.mayconbordin.oauth2.client;

/**
 *
 * @author Maycon Bordin <mayconbordin@gmail.com>
 */
public class UnsupportedContentType extends OAuth2Exception {

    public UnsupportedContentType(String contentType) {
        super("Cannot handle " + contentType + " content type. Supported content "
                + "types include JSON, XML and URLEncoded");
    }
    
}
