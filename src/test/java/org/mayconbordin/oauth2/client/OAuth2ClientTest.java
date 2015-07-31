package org.mayconbordin.oauth2.client;

import java.io.IOException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Maycon Bordin <mayconbordin@gmail.com>
 */
public class OAuth2ClientTest extends OAuth2BaseTest {
    
    @Before
    public void setUp() throws IOException {
        CloseableHttpClient httpClient = mockHttpClient();
        OAuth2Utils.setHttpClient(httpClient);
    }

    @Test
    public void testGetAccessTokenGrantPassword() throws Exception {
        System.out.println("testGetAccessTokenGrantPassword");

        OAuth2Client client = OAuth2Client.withPasswordGrant(
            "oauth_user", "oauth_user_password",
            "client1id", "client1secret",
            "http://localhost/api/oauth/access_token");
        
        AccessToken token = client.getAccessToken();
        
        assertNotNull(token);
        assertEquals(accessTokenRefreshResponse.get(OAuth2Constants.ACCESS_TOKEN), token.getAccessToken());
        assertEquals(accessTokenRefreshResponse.get(OAuth2Constants.TOKEN_TYPE), token.getTokenType());
        assertEquals(accessTokenRefreshResponse.get(OAuth2Constants.REFRESH_TOKEN), token.getRefreshToken());
    }
    
    @Test(expected = UnauthorizedClientException.class)
    public void testGetAccessTokenGrantPasswordWrongUserCredentials() throws Exception {
        System.out.println("testGetAccessTokenGrantPasswordWrongUserCredentials");

        OAuth2Client client = OAuth2Client.withPasswordGrant(
            "oauth_user_wrong", "oauth_user_password",
            "client1id", "client1secret",
            "http://localhost/api/oauth/access_token");
        
        AccessToken token = client.getAccessToken();
    }
    
    @Test
    public void testGetAccessTokenGrantClientCredentials() throws Exception {
        System.out.println("testGetAccessTokenGrantClientCredentials");

        OAuth2Client client = OAuth2Client.withClientCredentialsGrant(
            "client1id", "client1secret",
            "http://localhost/api/oauth/access_token");
        
        AccessToken token = client.getAccessToken();

        assertNotNull(token);
        assertEquals(accessTokenResponse.get(OAuth2Constants.ACCESS_TOKEN), token.getAccessToken());
        assertEquals(accessTokenResponse.get(OAuth2Constants.TOKEN_TYPE), token.getTokenType());
        assertEquals(accessTokenResponse.get(OAuth2Constants.REFRESH_TOKEN), token.getRefreshToken());
    }
    
    @Test(expected = UnauthorizedClientException.class)
    public void testGetAccessTokenGrantClientCredentialsWrongCredentials() throws Exception {
        System.out.println("testGetAccessTokenGrantClientCredentialsWrongCredentials");

        OAuth2Client client = OAuth2Client.withClientCredentialsGrant(
            "client1id_wrong", "client1secret",
            "http://localhost/api/oauth/access_token");
        
        AccessToken token = client.getAccessToken();
    }
    
    @Test(expected = ResourceNotFoundException.class)
    public void testGetAccessTokenGrantClientCredentialsWrongUrl() throws Exception {
        System.out.println("testGetAccessTokenGrantClientCredentialsMissingFields");

        OAuth2Client client = OAuth2Client.withClientCredentialsGrant(
            "client1id", "client1secret",
            "http://localhost/api/oauth/access_token_wrong");
        
        AccessToken token = client.getAccessToken();
    }
    
}
