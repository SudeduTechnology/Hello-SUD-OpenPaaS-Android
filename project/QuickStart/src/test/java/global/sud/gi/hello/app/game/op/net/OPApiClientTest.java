package global.sud.gi.hello.app.game.op.net;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class OPApiClientTest {

    @Test
    public void parsesAnonymousLoginResponse() throws Exception {
        OPLoginResponse response = OPApiClient.parseLoginResponse(
                "{\"appToken\":\"token\",\"appTokenExpiresIn\":3600,"
                        + "\"uid\":10000,\"nickname\":\"小星星\","
                        + "\"avatarUrl\":\"https://example.com/a.png\","
                        + "\"casdoorBound\":false}"
        );

        assertEquals("token", response.appToken);
        assertEquals(3600L, response.appTokenExpiresIn);
        assertEquals(10000L, response.uid);
        assertEquals("小星星", response.nickname);
        assertFalse(response.casdoorBound);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsLoginWithoutToken() throws Exception {
        OPApiClient.parseLoginResponse(
                "{\"appToken\":\"\",\"appTokenExpiresIn\":3600,"
                        + "\"uid\":10000,\"nickname\":\"用户\","
                        + "\"avatarUrl\":null,\"casdoorBound\":false}"
        );
    }

    @Test
    public void parsesUserSignatureIdentity() throws Exception {
        OPUserSignatureResponse response = OPApiClient.parseUserSignatureResponse(
                "{\"appId\":\"app-id\",\"user\":{\"id\":10000,"
                        + "\"name\":\"用户\",\"avatar\":\"\"},"
                        + "\"userSignature\":\"signature\","
                        + "\"expiresAt\":\"2026-10-13T10:00:00.000Z\"}"
        );

        assertEquals("app-id", response.appId);
        assertEquals(10000L, response.uid);
        assertEquals("signature", response.userSignature);
    }
}
