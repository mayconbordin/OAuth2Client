package org.mayconbordin.oauth2.client;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that handles the requests to the OAuth2 server.
 * 
 * @author Maycon Bordin <mayconbordin@gmail.com>
 */
public class OAuth2Utils {
    private static final Logger LOG = LoggerFactory.getLogger(OAuth2Utils.class);
    private static CloseableHttpClient httpClient;

    /**
     * Get a protected resource using an access token.
     * 
     * @param token The token to be used for authentication.
     * @param url The path to the resource.
     * @return The contents of the resource.
     * @throws OAuth2Exception 
     */
    public static String getProtectedResource(AccessToken token, String url) throws OAuth2Exception {
        HttpUriRequest request = new OAuth2RequestBuilder(HttpGet.METHOD_NAME, url, token).build();

        HttpResponse response = null;
        String responseString = "";
        
        try {
            response = getHttpClient().execute(request);
            int code = response.getStatusLine().getStatusCode();
            responseString = ContentHandler.readHttpEntity(response.getEntity());
            
            if (code >= 400) {
                throw OAuth2Exception.fromHttpCode(code, responseString);
            }
        } catch (IOException e) {
            LOG.error("IO error: " + e.getMessage());
            throw new OAuth2Exception("An error ocurred while executing the request.", e);
        }
        
        return responseString;
    }

    /**
     * Get an access token based on the given configurations.
     * 
     * @param config The configuration to be used for obtaining the access token.
     * @return The obtained access token.
     * @throws OAuth2Exception 
     */
    public static AccessToken getAccessToken(OAuth2Config config) throws OAuth2Exception {
        HttpUriRequest request = new OAuth2RequestBuilder(HttpPost.METHOD_NAME, config).build();
        
        HttpResponse response   = null;
        AccessToken accessToken = null;
        
        try {
            response = getHttpClient().execute(request);
            int code = response.getStatusLine().getStatusCode();
            
            if (code >= 400) {
                request = new OAuth2RequestBuilder(HttpPost.METHOD_NAME, config)
                        .authorization(config.getUsername(), config.getPassword())
                        .build();

                response = getHttpClient().execute(request);
                code = response.getStatusLine().getStatusCode();
                
                if (code >= 400) {
                    request = new OAuth2RequestBuilder(HttpPost.METHOD_NAME, config)
                            .authorization(config.getClientId(), config.getClientSecret())
                            .build();

                    response = getHttpClient().execute(request);
                    code = response.getStatusLine().getStatusCode();

                    if (code >= 400) {
                        throw OAuth2Exception.fromHttpCode(code, ContentHandler.readHttpEntity(response.getEntity()));
                    }
                }
            }
            
            accessToken = new AccessToken(ContentHandler.handleResponse(response));
        } catch (IOException e) {
            LOG.error("IO error: " + e.getMessage());
            throw new OAuth2Exception("An error ocurred while executing the request.", e);
        }

        return accessToken;
    }

    /**
     * Refresh an access token.
     * 
     * @param token The token to be refreshed.
     * @param config The configuration to be used to refresh the token.
     * @return The refreshed token.
     * @throws OAuth2Exception 
     */
    public static AccessToken refreshAccessToken(AccessToken token, OAuth2Config config) throws OAuth2Exception {
        HttpUriRequest request = new OAuth2RequestBuilder(HttpPost.METHOD_NAME, config, token).build();
        
        HttpResponse response = null;
        AccessToken accessToken     = null;
        
        try {
            response = getHttpClient().execute(request);
            int code = response.getStatusLine().getStatusCode();
            
            if (code >= 400) {
                request = new OAuth2RequestBuilder(HttpPost.METHOD_NAME, config)
                        .authorization(config.getClientId(), config.getClientSecret())
                        .build();

                response = getHttpClient().execute(request);
                code = response.getStatusLine().getStatusCode();

                if (code >= 400) {
                    throw OAuth2Exception.fromHttpCode(code, ContentHandler.readHttpEntity(response.getEntity()));
                }
            }
            
            accessToken = new AccessToken(ContentHandler.handleResponse(response));
        } catch (IOException e) {
            LOG.error("IO error: " + e.getMessage());
            throw new OAuth2Exception("An error ocurred while executing the request.", e);
        }

        return accessToken;
    }
    
    protected static CloseableHttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = HttpClients.createDefault();
        }
        return httpClient;
    }
    
    public static void setHttpClient(CloseableHttpClient client) {
        httpClient = client;
    }
}