package org.mayconbordin.oauth2.client;

/**
 * The configuration for making requests to the OAuth2 provider.
 * 
 * @author Maycon Bordin <mayconbordin@gmail.com>
 */
public class OAuth2Config {
    private final String scope;
    private final String grantType;
    private final String clientId;
    private final String clientSecret;
    private final String username;
    private final String password;
    private final String urlAccessToken;

    private OAuth2Config(Builder builder) {
        this.username = builder.username;
        this.password = builder.password;
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;
        this.urlAccessToken = builder.urlAccessToken;
        this.scope = builder.scope;
        this.grantType = builder.grantType;
    }
    
    public String getScope() {
        return scope;
    }
    
    public String getGrantType() {
        return grantType;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public String getClientSecret() {
        return clientSecret;
    }
    
    public String getUrlAccessToken() {
        return urlAccessToken;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }

    /**
     * Class used for building the {@link OAuth2Config}.
     */
    public static class Builder {
        private String scope;
        private String grantType;
        private String clientId;
        private String clientSecret;
        private String username;
        private String password;
        private String urlAccessToken;

        /**
         * Create a new configuration builder.
         * 
         * @param clientId
         * @param clientSecret
         * @param urlAccessToken 
         */
        public Builder(String clientId, String clientSecret, String urlAccessToken) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.urlAccessToken = urlAccessToken;
        }

        /**
         * Create a new configuration builder from an existing configuration.
         * 
         * @param config The existing configuration.
         */
        public Builder(OAuth2Config config) {
            this.username = config.getUsername();
            this.password = config.getPassword();
            this.clientId = config.getClientId();
            this.clientSecret = config.getClientSecret();
            this.urlAccessToken = config.getUrlAccessToken();
            this.grantType = config.getGrantType();
            this.scope = config.getScope();
        }

        /**
         * Set the grant type of the configuration.
         * 
         * @param grantType
         * @return 
         */
        public Builder grantType(String grantType) {
            this.grantType = grantType;
            return this;
        }

        /**
         * Set the scope of the configuration.
         * 
         * @param scope A comma-separated list of scopes.
         * @return 
         */
        public Builder scope(String scope) {
            this.scope = scope;
            return this;
        }
        
        /**
         * Set the user credentials of the configuration.
         * 
         * @param username
         * @param password
         * @return 
         */
        public Builder credentials(String username, String password) {
            this.username = username;
            this.password = password;
            return this;
        }
        
        /**
         * Build the configuration.
         * 
         * @return 
         */
        public OAuth2Config build() {
            return new OAuth2Config(this); 
        }
    }
}
