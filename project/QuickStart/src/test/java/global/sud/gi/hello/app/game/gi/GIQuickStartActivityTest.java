package global.sud.gi.hello.app.game.gi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

import android.app.Application;
import android.content.Intent;
import android.widget.EditText;

import com.blankj.utilcode.util.Utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import global.sud.gi.hello.R;
import global.sud.gi.hello.app.base.AppConfig;
import global.sud.gi.hello.app.common.utils.GlobalSP;
import global.sud.gi.hello.app.ui.main.MainActivity;
import global.sud.gi.hello.app.game.gi.data.GIGameModel;
import global.sud.gi.hello.app.game.gi.data.GIGameRepository;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, application = Application.class)
public class GIQuickStartActivityTest {

    private ActivityController<? extends android.app.Activity> controller;

    @Before
    public void setUp() {
        Utils.init(RuntimeEnvironment.getApplication());
        GlobalSP.getSP().clear();
    }

    @After
    public void tearDown() {
        if (controller != null) {
            controller.destroy();
        }
        GlobalSP.getSP().clear();
    }

    @Test
    public void homeEntryOpensGIQuickStart() {
        ActivityController<MainActivity> mainController = Robolectric.buildActivity(MainActivity.class).setup();
        controller = mainController;

        mainController.get().findViewById(R.id.btn_gi_game).performClick();

        Intent intent = shadowOf(mainController.get()).getNextStartedActivity();
        assertNotNull(intent);
        assertEquals(GIQuickStartActivity.class.getName(), intent.getComponent().getClassName());
    }

    @Test
    public void quickStartUsesConfiguredDefaultRoomAndCompleteGameList() {
        ActivityController<GIQuickStartActivity> quickStartController =
                Robolectric.buildActivity(GIQuickStartActivity.class).setup();
        controller = quickStartController;

        EditText roomId = quickStartController.get().findViewById(R.id.et_room_id);
        assertEquals(AppConfig.GI_DEFAULT_ROOM_ID, roomId.getHint().toString());

        List<GIGameModel> games = GIGameRepository.getGameList();
        assertEquals(26, games.size());
        assertEquals(1461227817776713818L, games.get(0).gameId);
        assertEquals(1585556944972623874L, games.get(25).gameId);

        Set<Long> uniqueIds = new HashSet<>();
        for (GIGameModel game : games) {
            assertTrue(game.gameId > 0);
            assertTrue(game.homeGamePic != 0);
            assertTrue(game.gamePic != 0);
            uniqueIds.add(game.gameId);
        }
        assertEquals(games.size(), uniqueIds.size());
    }

    @Test
    public void customRoomEntersEmptyGIroom() {
        ActivityController<GIQuickStartActivity> quickStartController =
                Robolectric.buildActivity(GIQuickStartActivity.class).setup();
        controller = quickStartController;

        EditText roomId = quickStartController.get().findViewById(R.id.et_room_id);
        roomId.setText("20001");
        quickStartController.get().findViewById(R.id.tv_enter).performClick();

        Intent intent = shadowOf(quickStartController.get()).getNextStartedActivity();
        assertNotNull(intent);
        assertEquals(GIGameActivity.class.getName(), intent.getComponent().getClassName());
        assertEquals("20001", intent.getStringExtra("roomId"));
        assertEquals(0L, intent.getLongExtra("gameId", -1));
    }
}
