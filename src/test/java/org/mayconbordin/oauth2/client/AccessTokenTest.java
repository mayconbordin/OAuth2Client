package org.mayconbordin.oauth2.client;

import java.io.IOException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Maycon Bordin <mayconbordin@gmail.com>
 */
public class AccessTokenTest extends OAuth2BaseTest {

    @Before
    public void setUp() throws IOException {
        CloseableHttpClient httpClient = mockHttpClient();
        OAuth2Utils.setHttpClient(httpClient);
    }

    @Test
    public void testGetResource() throws Exception {
        System.out.println("testGetResource");
        
        OAuth2Client client = OAuth2Client.withPasswordGrant(
            "oauth_user", "oauth_user_password",
            "client1id", "client1secret",
            "http://localhost/api/oauth/access_token");

        AccessToken token = client.getAccessToken();
        String resource = token.getResource("http://localhost/api/user_info");
        
        JSONObject obj = (JSONObject) new JSONParser().parse(resource);
        
        assertEquals(userInfoResponse.get("username"), obj.get("username"));
        assertEquals(userInfoResponse.get("email"), obj.get("email"));
    }

    @Test
    public void testRefresh() throws Exception {
        System.out.println("testRefresh");
        
        OAuth2Client client = OAuth2Client.withPasswordGrant(
            "oauth_user", "oauth_user_password",
            "client1id", "client1secret",
            "http://localhost/api/oauth/access_token");

        AccessToken token = client.getAccessToken();
        AccessToken newToken = token.refresh(client);
        
        assertNotNull(newToken);
        assertEquals(accessTokenRefreshResponse.get(OAuth2Constants.ACCESS_TOKEN), newToken.getAccessToken());
        assertEquals(accessTokenRefreshResponse.get(OAuth2Constants.TOKEN_TYPE), newToken.getTokenType());
        assertEquals(accessTokenRefreshResponse.get(OAuth2Constants.REFRESH_TOKEN), newToken.getRefreshToken());
    }

}
