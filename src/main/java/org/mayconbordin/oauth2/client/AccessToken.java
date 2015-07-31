package org.mayconbordin.oauth2.client;

import java.util.Map;
import static org.mayconbordin.oauth2.client.OAuth2Constants.*;

/**
 * The access token.
 * 
 * @author Maycon Bordin <mayconbordin@gmail.com>
 */
public class AccessToken {
    private final long expiresIn;
    private final long expiresAt;
    private final String tokenType;
    private final String refreshToken;
    private final String accessToken;
    
    /**
     * Create an access token from a map.
     * @param map Required keys: {@link #EXPIRES_IN}, {@link #TOKEN_TYPE}, {@link #ACCESS_TOKEN}.
     *            Optional keys: {@link #REFRESH_TOKEN}.
     */
    public AccessToken(Map<String, Object> map) {
        this((Long) map.get(EXPIRES_IN), (String) map.get(TOKEN_TYPE),
             (String) map.get(REFRESH_TOKEN), (String) map.get(ACCESS_TOKEN));
    }

    /**
     * Create an access token.
     * 
     * @param expiresIn The life expectancy of the token in seconds.
     * @param tokenType The type of token.
     * @param refreshToken The refresh token value.
     * @param accessToken The access token value.
     */
    public AccessToken(long expiresIn, String tokenType, String refreshToken, String accessToken) {
        this.expiresIn    = expiresIn;
        this.tokenType    = tokenType;
        this.refreshToken = refreshToken;
        this.accessToken  = accessToken;
        this.expiresAt    = (expiresIn * 1000) + System.currentTimeMillis();
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Checks if the access token might have expired, by comparing  the time the token
     * was created plus {@link #expiresIn} and the current time.
     * @return True if the token expired, false otherwise.
     */
    public boolean isExpired() {
        return (System.currentTimeMillis() >= this.getExpiresAt());
    }

    /**
     * Get a resource using this token.
     * 
     * @param path The full path to the resource.
     * @return The content of the resource.
     * @throws OAuth2Exception 
     */
    public String getResource(String path) throws OAuth2Exception {
        return OAuth2Utils.getProtectedResource(this, path);
    }

    /**
     * Refresh this token.
     * 
     * @param client The client for refreshing the token, the same used to create this token.
     * @return The refreshed token.
     * @throws OAuth2Exception 
     */
    public AccessToken refresh(OAuth2Client client) throws OAuth2Exception {
        OAuth2Config oauthConfig = new OAuth2Config.Builder(client.getConfig())
                .grantType(OAuth2Constants.GRANT_REFRESH_TOKEN)
                .build();
        
        return OAuth2Utils.refreshAccessToken(this, oauthConfig);
    }

    @Override
    public String toString() {
        return "Token{" + "expiresIn=" + expiresIn + ", expiresAt=" + expiresAt 
                + ", tokenType=" + tokenType + ", refreshToken=" + refreshToken 
                + ", accessToken=" + accessToken + '}';
    }
}
