package org.mayconbordin.oauth2.client;

/**
 * The OAuth2 client.
 * 
 * @author Maycon Bordin <mayconbordin@gmail.com>
 */
public class OAuth2Client {
    private final OAuth2Config config;
    
    private OAuth2Client(OAuth2Config config) {
        this.config = config;
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
    
    /**
     * Create a client with client credentials grant type.
     * 
     * @param clientId
     * @param clientSecret
     * @param urlAccessToken
     * @return 
     */
    public static OAuth2Client withClientCredentialsGrant(String clientId,
            String clientSecret, String urlAccessToken) {
        return withClientCredentialsGrant(clientId, clientSecret, null, urlAccessToken);
    }
    
    /**
     * Create a client with client credentials grant type.
     * 
     * @param clientId
     * @param clientSecret
     * @param scope
     * @param urlAccessToken
     * @return 
     */
    public static OAuth2Client withClientCredentialsGrant(String clientId,
            String clientSecret, String scope, String urlAccessToken) {
        OAuth2Config config = new OAuth2Config.Builder(clientId, clientSecret, urlAccessToken)
                .grantType(OAuth2Constants.GRANT_CLIENT_CREDENTIALS)
                .scope(scope)
                .build();
        
        return new OAuth2Client(config);
    }
    
    /**
     * Create a client with password grant type.
     * 
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @param urlAccessToken
     * @return 
     */
    public static OAuth2Client withPasswordGrant(String username, String password,
            String clientId, String clientSecret, String urlAccessToken) {
        return withPasswordGrant(username, password, clientId, clientSecret, null, urlAccessToken);
    }
    
    /**
     * Create a client with password grant type.
     * 
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @param scope
     * @param urlAccessToken
     * @return 
     */
    public static OAuth2Client withPasswordGrant(String username, String password,
            String clientId, String clientSecret, String scope, String urlAccessToken) {
        OAuth2Config config = new OAuth2Config.Builder(clientId, clientSecret, urlAccessToken)
                .grantType(OAuth2Constants.GRANT_PASSWORD)
                .credentials(username, password)
                .scope(scope)
                .build();
        
        return new OAuth2Client(config);
    }
}
