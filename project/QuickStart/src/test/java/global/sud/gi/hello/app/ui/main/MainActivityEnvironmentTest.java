package global.sud.gi.hello.app.ui.main;

import android.app.Application;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blankj.utilcode.util.Utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import global.sud.gi.hello.BuildConfig;
import global.sud.gi.hello.R;
import global.sud.gi.hello.app.common.utils.GlobalSP;
import global.sud.gi.hello.app.game.op.OPListActivity;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, application = Application.class)
public class MainActivityEnvironmentTest {

    private ActivityController<MainActivity> controller;

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
    public void headerShowsPackagedVersions() throws Exception {
        controller = Robolectric.buildActivity(MainActivity.class).create().start().resume();
        TextView buildInfo = controller.get().findViewById(R.id.sud_demo_build_info);
        String aarVersion = String.valueOf(Class.forName("global.sud.mediation.version.Version").getField("kVersion").get(null));
        assertEquals("APK: " + BuildConfig.VERSION_NAME + " " + BuildConfig.BUILD_TYPE + "\nAAR: " + aarVersion, buildInfo.getText().toString());
    }

    @Test
    public void opEntryIsImmediatelyAboveGiEntry() {
        controller = Robolectric.buildActivity(MainActivity.class).create().start().resume();
        ViewGroup container = (ViewGroup) controller.get()
                .findViewById(R.id.btn_op)
                .getParent();
        int opIndex = container.indexOfChild(controller.get().findViewById(R.id.btn_op));
        int giIndex = container.indexOfChild(controller.get().findViewById(R.id.btn_gi_game));

        assertEquals(3, container.getChildCount());
        assertEquals(giIndex - 1, opIndex);

        controller.get().findViewById(R.id.btn_op).performClick();
        Intent started = Shadows.shadowOf((Application) RuntimeEnvironment.getApplication())
                .getNextStartedActivity();
        assertEquals(OPListActivity.class.getName(), started.getComponent().getClassName());
    }
}
