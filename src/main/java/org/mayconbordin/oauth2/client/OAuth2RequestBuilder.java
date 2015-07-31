package org.mayconbordin.oauth2.client;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

/**
 * Wrapper around the {@link RequestBuilder} as a builder itself, with methods for
 * easing the building of OAuth2 requests.
 * 
 * @author Maycon Bordin <mayconbordin@gmail.com>
 */
public class OAuth2RequestBuilder {
    private final String methodName;
    private final String url;
    private final OAuth2Config config;
    private final AccessToken token;
    private final Map<String, String> headers;

    public OAuth2RequestBuilder(String methodName, String url, OAuth2Config config, AccessToken token) {
        this.methodName = methodName;
        this.url = url;
        this.config = config;
        this.token = token;
        headers = new HashMap<>();
    }

    public OAuth2RequestBuilder(String methodName, OAuth2Config config, AccessToken token) {
        this(methodName, config.getUrlAccessToken(), config, token);
    }

    public OAuth2RequestBuilder(String methodName, OAuth2Config config) {
        this(methodName, config, null);
    }

    public OAuth2RequestBuilder(String methodName, String url) {
        this(methodName, url, null, null);
    }

    public OAuth2RequestBuilder(String methodName, String url, OAuth2Config config) {
        this(methodName, url, config, null);
    }

    public OAuth2RequestBuilder(String methodName, String url, AccessToken token) {
        this(methodName, url, null, token);
    }

    public OAuth2RequestBuilder authorization(AccessToken token) {
        headers.put(OAuth2Constants.AUTHORIZATION, getAuthorizationHeaderForAccessToken(token.getAccessToken()));
        return this;
    }

    public OAuth2RequestBuilder authorization(String username, String password) {
        headers.put(OAuth2Constants.AUTHORIZATION, getBasicAuthorizationHeader(username, password));
        return this;
    }
    
    public OAuth2RequestBuilder header(String name, String value) {
        headers.put(name, value);
        return this;
    }
    
    /**
     * Build the HTTP request.
     * 
     * <p>If {@link #token} is not null and the grant type is not {@link OAuth2Constants#GRANT_REFRESH_TOKEN}, then
     * the authorization header is added to the request.</p>
     * 
     * <p>If you called {@link #authorization(org.mayconbordin.oauth2.client.AccessToken)}, the authorization
     * header will be on the request, regardless of the grant type.</p>
     * 
     * <p>If {@link #config} is not null, the client credentials and grant type will be added
     * to the HTTP entity. If the grant type is {@link OAuth2Constants#GRANT_PASSWORD} the
     * user credentials will also be added to the HTTP entity. And if the grant type is
     * {@link OAuth2Constants#GRANT_REFRESH_TOKEN} the refresh token will be added to the entity.</p>
     * 
     * <p>If the {@link OAuth2Config#scope} are not empty, they will be added to the HTTP entity.</p>
     * 
     * @return 
     */
    public HttpUriRequest build() {
        RequestBuilder builder = RequestBuilder.create(methodName).setUri(url);

        // Add the authorization token, except for the refresh token grant
        if (token != null && (config == null || !OAuth2Constants.GRANT_REFRESH_TOKEN.equals(config.getGrantType()))) {
            String tokenHeader = getAuthorizationHeaderForAccessToken(token.getAccessToken());
            builder.addHeader(OAuth2Constants.AUTHORIZATION, tokenHeader);
        }
        
        if (config != null) {
            List<NameValuePair> formData = buildNameValuePairs(
                OAuth2Constants.CLIENT_ID, config.getClientId(),
                OAuth2Constants.CLIENT_SECRET, config.getClientSecret(),
                OAuth2Constants.GRANT_TYPE, config.getGrantType()
            );

            // Add user credentials if is the password grant
            if (OAuth2Constants.GRANT_PASSWORD.equals(config.getGrantType())) {
                buildNameValuePairs(formData,
                    OAuth2Constants.USERNAME, config.getUsername(),
                    OAuth2Constants.PASSWORD, config.getPassword()
                );
            }
            
            // Add the refresh token if is the refresh token grant
            else if (OAuth2Constants.GRANT_REFRESH_TOKEN.equals(config.getGrantType()) 
                    && token != null) {
                buildNameValuePairs(formData,
                    OAuth2Constants.REFRESH_TOKEN, token.getRefreshToken()
                );
            }

            // Add the scope, if exists
            if (config.getScope() != null && config.getScope().trim().length() > 0) {
                buildNameValuePairs(formData, OAuth2Constants.SCOPE, config.getScope());
            }

            builder.setEntity(new UrlEncodedFormEntity(formData, StandardCharsets.UTF_8));
        }

        // Apply headers
        for (Map.Entry<String, String> e : headers.entrySet()) {
            builder.addHeader(new BasicHeader(e.getKey(), e.getValue()));
        }
        
        return builder.build();
    }
    
    protected List<NameValuePair> buildNameValuePairs(String...pairs) {
        return buildNameValuePairs(new ArrayList<NameValuePair>(pairs.length/2), pairs);
    }

    protected List<NameValuePair> buildNameValuePairs(List<NameValuePair> data, String...pairs) {
        if (pairs.length % 2 != 0) {
            throw new IllegalArgumentException("List of arguments must be in pairs.");
        }

        for (int i=0; i<pairs.length; i+=2) {
            data.add(new BasicNameValuePair(pairs[i], pairs[i+1]));
        }

        return data;
    }

    protected String getAuthorizationHeaderForAccessToken(String accessToken) {
        return OAuth2Constants.BEARER + " " + accessToken;
    }

    protected String getBasicAuthorizationHeader(String username, String password) {
        return OAuth2Constants.BASIC + " " + encodeCredentials(username, password);
    }
    
    protected static String encodeCredentials(String username, String password) {
        String cred = username + ":" + password;
        
        byte[] encodedBytes = Base64.encodeBase64(cred.getBytes());
        String encodedValue = new String(encodedBytes);

        return encodedValue;
    }
}
