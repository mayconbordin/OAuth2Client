package org.mayconbordin.oauth2.client;

/**
 *
 * @author Maycon Bordin <mayconbordin@gmail.com>
 */
public class ParseErrorException extends OAuth2Exception {
    private String contentType;
    private String content;
    
    public ParseErrorException(String contentType, String content, Throwable cause) {
        super("Error parsing content of type "+contentType, cause);
        this.contentType = contentType;
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public String getContent() {
        return content;
    }
}
