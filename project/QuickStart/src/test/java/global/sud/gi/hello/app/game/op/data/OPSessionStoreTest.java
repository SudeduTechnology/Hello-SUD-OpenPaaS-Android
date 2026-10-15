package global.sud.gi.hello.app.game.op.data;

import android.app.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, application = Application.class)
public class OPSessionStoreTest {

    private OPSessionStore store;

    @Before
    public void setUp() {
        store = new OPSessionStore(RuntimeEnvironment.getApplication());
        store.clearSession();
    }

    @Test
    public void validSessionIsRestoredWithAllProfileFields() {
        long now = System.currentTimeMillis();
        OPSession session = new OPSession(
                "token",
                now + 20_000L,
                10_001L,
                "小星星",
                "https://example.com/avatar.png",
                false
        );

        assertTrue(store.save(session));
        OPSession restored = store.getValidSession(now);

        assertNotNull(restored);
        assertEquals(10_001L, restored.uid);
        assertEquals("小星星", restored.nickname);
        assertEquals("https://example.com/avatar.png", restored.avatarUrl);
    }

    @Test
    public void expiredSessionIsNotReusable() {
        OPSession session = new OPSession(
                "token",
                System.currentTimeMillis() + 10_000L,
                10_001L,
                "用户",
                "",
                false
        );
        assertTrue(store.save(session));
        assertNull(store.getValidSession(session.expiresAtEpochMs));
    }

    @Test
    public void idempotencyKeyIsStableAcrossRetries() {
        String first = store.getOrCreateIdempotencyKey();
        String second = new OPSessionStore(RuntimeEnvironment.getApplication())
                .getOrCreateIdempotencyKey();

        assertNotNull(first);
        assertTrue(first.length() >= 8);
        assertEquals(first, second);
    }
}
