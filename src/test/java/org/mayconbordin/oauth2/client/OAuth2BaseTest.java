package org.mayconbordin.oauth2.client;

import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.json.simple.JSONObject;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 *
 * @author Maycon Bordin <mayconbordin@gmail.com>
 */
public class OAuth2BaseTest {    
    protected String urlAccessToken = "http://localhost/api/oauth/access_token";
    protected String urlUserInfo = "http://localhost/api/user_info";
    protected String clientId = "client1id";
    protected String clientSecret = "client1secret";
    protected String username = "oauth_user";
    protected String password = "oauth_user_password";
    
    protected Map<String, Object> accessTokenResponse = new ImmutableMap.Builder<String, Object>()
            .put("access_token", "B4Aqq4LPqCQi9onG0asXu1KgwiOQaSeR8SeTt20L")
            .put("token_type", "Bearer")
            .put("expires_in", 3600)
            .build();
    
    protected Map<String, Object> accessTokenRefreshResponse = new ImmutableMap.Builder<String, Object>()
            .put("access_token", "uP6WPncIMox6baxNjxihw8aUeObS3WTjSnPQ0efu")
            .put("token_type", "Bearer")
            .put("expires_in", 3600)
            .put("refresh_token", "5XA1GXSgeynoN4T9AnWkaqPUUq0hDFK5ndhTNZqP")
            .build();
    
    protected Map<String, Object> invalidClientResponse = new ImmutableMap.Builder<String, Object>()
            .put("error", "invalid_client")
            .put("error_description", "Client authentication failed.")
            .build();
    
    protected Map<String, Object> invalidCredentialsResponse = new ImmutableMap.Builder<String, Object>()
            .put("error", "invalid_credentials")
            .put("error_description", "The user credentials were incorrect.")
            .build();
    
    protected Map<String, Object> invalidGrantResponse = new ImmutableMap.Builder<String, Object>()
            .put("error", "unsupported_grant_type")
            .put("error_description", "The authorization grant type \"passwords\" is not supported by the authorization server.")
            .build();
    
    protected Map<String, Object> invalidRequestResponse = new ImmutableMap.Builder<String, Object>()
            .put("error", "invalid_request")
            .put("error_description", "The request is missing a required parameter, includes an invalid parameter value, includes a parameter more than once, or is otherwise malformed. Check the \"refresh_token\" parameter.")
            .build();
    
    protected Map<String, Object> invalidRefreshTokenResponse = new ImmutableMap.Builder<String, Object>()
            .put("error", "invalid_request")
            .put("error_description", "The refresh token is invalid.")
            .build();
    
    protected Map<String, Object> accessDeniedResponse = new ImmutableMap.Builder<String, Object>()
            .put("error", "access_denied")
            .put("error_description", "The resource owner or authorization server denied the request.")
            .build();
    
    protected Map<String, Object> invalidScopeResponse = new ImmutableMap.Builder<String, Object>()
            .put("error", "invalid_scope")
            .put("error_description", "The requested scope is invalid, unknown, or malformed.")
            .build();
    
    protected Map<String, Object> userInfoResponse = new ImmutableMap.Builder<String, Object>()
            .put("username", "oauth_user")
            .put("email", "oauth_user@test.org")
            .build();


    protected Answer<Object> executeAnswer = new Answer<Object>() {
        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            HttpRequestBase request = invocation.getArgumentAt(0, HttpRequestBase.class);
            
            if (request instanceof HttpEntityEnclosingRequestBase) {
                return handleRequestWithEntity((HttpEntityEnclosingRequestBase) request);
            } else {
                return handleRequest(request);
            }
        }
    };
    
    protected CloseableHttpResponse handleRequest(HttpRequestBase request) throws Exception {
        String requestUrl = request.getURI().toString();
        Header authorization = request.getFirstHeader(OAuth2Constants.AUTHORIZATION); 
        
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        
        if (requestUrl.equals(urlUserInfo) && request.getMethod().equals("GET")) {
            // Check if authorization token is present and valid
            if (!checkAuthorizationToken(authorization)) {
                when(response.getEntity()).thenReturn(createHttpEntity(accessDeniedResponse));
                when(response.getStatusLine()).thenReturn(createStatusLine(401));
                return response;
            }
            
            when(response.getEntity()).thenReturn(createHttpEntity(userInfoResponse));
            when(response.getStatusLine()).thenReturn(createStatusLine(200));
        } else {
            when(response.getEntity()).thenReturn(new StringEntity("Not Found"));
            when(response.getStatusLine()).thenReturn(createStatusLine(404));
        }

        return response;
    }
    
    protected CloseableHttpResponse handleRequestWithEntity(HttpEntityEnclosingRequestBase request) throws Exception {
        String requestUrl = request.getURI().toString();

        //Header authorization = request.getFirstHeader(OAuth2Constants.AUTHORIZATION);            
        Map<String, Object> data = ContentHandler.handleEntity(request.getEntity());

        CloseableHttpResponse response = mock(CloseableHttpResponse.class);

        if (requestUrl.equals(urlAccessToken) && request.getMethod().equals("POST")) {
            // Check if grant type is supported
            if (!isClientCredentialsGrant(data) && !isPasswordGrant(data) && !isRefreshTokenGrant(data)) {
                when(response.getStatusLine()).thenReturn(createStatusLine(400));
                when(response.getEntity()).thenReturn(createHttpEntity(invalidGrantResponse));
                return response;
            }

            // Check if client credentials are valid
            if (!checkClientCredentials(data)) {
                when(response.getStatusLine()).thenReturn(createStatusLine(401));
                when(response.getEntity()).thenReturn(createHttpEntity(invalidClientResponse));
                return response;
            }

            // Check if user credentials are valid
            if (isPasswordGrant(data) && !checkUserCredentials(data)) {
                when(response.getStatusLine()).thenReturn(createStatusLine(401));
                when(response.getEntity()).thenReturn(createHttpEntity(invalidCredentialsResponse));
                return response;
            }

            // Response for client_credentials grant
            if (isClientCredentialsGrant(data)) {
                when(response.getEntity()).thenReturn(createHttpEntity(accessTokenResponse));
            }

            // Response for password grant
            else if (isPasswordGrant(data)) {
                when(response.getEntity()).thenReturn(createHttpEntity(accessTokenRefreshResponse));
            }

            // Response for refresh token grant
            else if (isRefreshTokenGrant(data)) {
                // Check if refresh token exists
                if (!data.containsKey(OAuth2Constants.REFRESH_TOKEN)) {
                    when(response.getStatusLine()).thenReturn(createStatusLine(400));
                    when(response.getEntity()).thenReturn(createHttpEntity(invalidRequestResponse));
                    return response;
                }

                // Check if refresh token is valid
                else if (!checkRefreshToken(data)) {
                    when(response.getStatusLine()).thenReturn(createStatusLine(400));
                    when(response.getEntity()).thenReturn(createHttpEntity(invalidRefreshTokenResponse));
                    return response;
                }

                when(response.getEntity()).thenReturn(createHttpEntity(accessTokenRefreshResponse));
            }

            when(response.getStatusLine()).thenReturn(createStatusLine(200));

            return response;
        } else {
            when(response.getEntity()).thenReturn(new StringEntity("Not Found"));
            when(response.getStatusLine()).thenReturn(createStatusLine(404));
        }

        return response;
    }
    
    protected boolean isPasswordGrant(Map<String, Object> data) {
        return (data.get(OAuth2Constants.GRANT_TYPE).equals(OAuth2Constants.GRANT_PASSWORD));
    }
    
    protected boolean isClientCredentialsGrant(Map<String, Object> data) {
        return (data.get(OAuth2Constants.GRANT_TYPE).equals(OAuth2Constants.GRANT_CLIENT_CREDENTIALS));
    }
    
    protected boolean isRefreshTokenGrant(Map<String, Object> data) {
        return (data.get(OAuth2Constants.GRANT_TYPE).equals(OAuth2Constants.GRANT_REFRESH_TOKEN));
    }
    
    protected boolean checkAuthorizationToken(Header authorization) {
        if (authorization == null) return false;
        
        String[] auth = authorization.getValue().split("\\s+");
        
        return (auth.length == 2 && auth[0].equals(OAuth2Constants.BEARER) 
                && auth[1].equals(accessTokenRefreshResponse.get(OAuth2Constants.ACCESS_TOKEN)));
    }
    
    protected boolean checkRefreshToken(Map<String, Object> data) {
        return data.get(OAuth2Constants.REFRESH_TOKEN).equals(accessTokenRefreshResponse.get(OAuth2Constants.REFRESH_TOKEN));
    }
    
    protected boolean checkUserCredentials(Map<String, Object> data) {
        return (data.get(OAuth2Constants.USERNAME).equals(username) 
                && data.get(OAuth2Constants.PASSWORD).equals(password));
    }
    
    protected boolean checkClientCredentials(Map<String, Object> data) {
        return (data.get(OAuth2Constants.CLIENT_ID).equals(clientId) 
                && data.get(OAuth2Constants.CLIENT_SECRET).equals(clientSecret));
    }
    
    protected Map<String, Object> readURLEncodedResponse(String content) {
        Map<String, Object> oauthResponse = new HashMap<>();

        List<NameValuePair> list = URLEncodedUtils.parse(content, StandardCharsets.UTF_8);

        for (NameValuePair pair : list) {
            oauthResponse.put(pair.getName(), pair.getValue());
        }

        return oauthResponse;
    }
    
    protected HttpEntity createHttpEntity(Map map) {
        String json = new JSONObject(map).toJSONString();
        return new StringEntity(json, ContentType.APPLICATION_JSON);
    }
    
    protected StatusLine createStatusLine(int code) {
        return new BasicStatusLine(new HttpVersion(1, 1), code, null);
    }
    
    protected CloseableHttpClient mockHttpClient() throws IOException {
        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        when(httpClient.execute(any(HttpUriRequest.class))).then(executeAnswer);
        
        return httpClient;
    }
}
