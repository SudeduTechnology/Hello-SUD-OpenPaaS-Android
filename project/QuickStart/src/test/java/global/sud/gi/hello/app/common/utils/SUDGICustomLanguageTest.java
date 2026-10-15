package global.sud.gi.hello.app.common.utils;

import android.app.Application;
import android.os.Looper;
import android.widget.ListView;
import android.widget.EditText;
import android.widget.CheckBox;

import com.blankj.utilcode.util.Utils;
import com.blankj.utilcode.util.SPUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;

import java.time.Duration;
import java.util.Locale;

import global.sud.gi.hello.R;
import global.sud.gi.hello.app.ui.main.SUDGILanguageActivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, application = Application.class, qualifiers = "en")
@LooperMode(LooperMode.Mode.PAUSED)
public class SUDGICustomLanguageTest {

    private Application application;
    private Locale originalLocale;

    @Before
    public void setUp() {
        originalLocale = Locale.getDefault();
        application = RuntimeEnvironment.getApplication();
        Utils.init(application);
        // UtilCode 缓存 SPUtils 实例；各用例显式隔离语言持久化状态。
        SPUtils.getInstance("Utils").clear(true);
        GlobalSP.getSP().clear(true);
    }

    @After
    public void tearDown() {
        SPUtils.getInstance("Utils").clear(true);
        GlobalSP.getSP().clear(true);
        Locale.setDefault(originalLocale);
    }

    @Test
    public void customLanguageRestoresSelectionAndCanSwitchBackToSystem() {
        SUDGILanguageConfig.selectCustomLanguage("  vi-VN  ");
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(32));
        assertEquals(-1, SUDGILanguageConfig.getSelectedIndex());
        assertEquals("vi-VN", SUDGILanguageConfig.getSelectedLabel(application));
        try (ActivityController<SUDGILanguageActivity> controller =
                     Robolectric.buildActivity(SUDGILanguageActivity.class).setup()) {
            SUDGILanguageActivity activity = controller.get();
            assertTrue(((CheckBox) activity.findViewById(R.id.sud_gi_language_custom_check)).isChecked());
            assertEquals("vi-VN", ((EditText) activity.findViewById(R.id.sud_gi_language_custom_input)).getText().toString());
            ListView list = activity.findViewById(R.id.sud_gi_language_list);
            assertEquals(ListView.INVALID_POSITION, list.getCheckedItemPosition());
            list.performItemClick(null, 0, 0);
            assertEquals(0, SUDGILanguageConfig.getSelectedIndex());
            assertNull(com.blankj.utilcode.util.LanguageUtils.getAppliedLanguage());
        }
    }

    @Test
    public void customCheckboxRejectsWhitespaceAndAppliesEnteredLanguage() {
        try (ActivityController<SUDGILanguageActivity> controller =
                     Robolectric.buildActivity(SUDGILanguageActivity.class).setup()) {
            SUDGILanguageActivity activity = controller.get();
            EditText input = activity.findViewById(R.id.sud_gi_language_custom_input);
            CheckBox check = activity.findViewById(R.id.sud_gi_language_custom_check);
            input.setText("   ");
            check.performClick();
            assertTrue(!check.isChecked());
            assertTrue(input.getError() != null);
            assertNull(com.blankj.utilcode.util.LanguageUtils.getAppliedLanguage());
            input.setText("vi");
            check.performClick();
            assertTrue(check.isChecked());
            assertEquals(-1, SUDGILanguageConfig.getSelectedIndex());
            assertEquals("vi", com.blankj.utilcode.util.LanguageUtils.getAppliedLanguage().getLanguage());
            assertEquals("vi", application.getResources().getConfiguration().getLocales().get(0).getLanguage());
        }
    }

}
