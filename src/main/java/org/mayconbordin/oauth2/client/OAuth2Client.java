package org.mayconbordin.oauth2.client;

/**
 * The OAuth2 client.
 * 
 * @author Maycon Bordin <mayconbordin@gmail.com>
 */
public class OAuth2Client {
    private final OAuth2Config config;

    /**
     * Create a new OAuth2 client.
     * 
     * @param grantType
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @param urlAccessToken 
     */
    public OAuth2Client(String grantType, String username, String password, String clientId,
            String clientSecret, String urlAccessToken) {
        config = new OAuth2Config.Builder(clientId, clientSecret, urlAccessToken)
                .grantType(grantType)
                .credentials(username, password)
                .build();
    }
    
    /**
     * Create a new OAuth2 client.
     * 
     * @param grantType
     * @param clientId
     * @param clientSecret
     * @param urlAccessToken 
     */
    public OAuth2Client(String grantType, String clientId, String clientSecret,
            String urlAccessToken) {
        config = new OAuth2Config.Builder(clientId, clientSecret, urlAccessToken)
                .grantType(grantType)
                .build();
    }

    /**
     * @return The configuration of the client.
     */
    public OAuth2Config getConfig() {
        return config;
    }

    /**
     * Get a new access token.
     * 
     * @return
     * @throws OAuth2Exception 
     */
    public AccessToken getAccessToken() throws OAuth2Exception {
        return OAuth2Utils.getAccessToken(config);
    }
}
