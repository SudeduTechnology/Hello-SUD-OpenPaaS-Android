package global.sud.gi.hello.app.common.utils;

import android.app.Application;
import android.content.res.Configuration;
import android.os.Looper;
import android.widget.ListView;
import android.widget.TextView;

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

import global.sud.gi.SUDGIWrapper.custom.ui.widget.dialog.ModalDialog;
import global.sud.gi.hello.R;
import global.sud.gi.hello.app.ui.main.SUDGILanguageActivity;
import global.sud.op.runtime.core.model.SUDOPShowModalParams;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, application = Application.class, qualifiers = "en")
@LooperMode(LooperMode.Mode.PAUSED)
public class SUDGILanguageConfigTest {

    private Application application;
    private Locale originalLocale;

    @Before
    public void setUp() {
        originalLocale = Locale.getDefault();
        application = RuntimeEnvironment.getApplication();
        Utils.init(application);
        // UtilCode 缓存 SPUtils 实例；各用例显式隔离语言持久化状态。
        SPUtils.getInstance("Utils").clear(true);
    }

    @After
    public void tearDown() {
        SPUtils.getInstance("Utils").clear(true);
        Locale.setDefault(originalLocale);
    }

    @Test
    public void freshInstallKeepsSystemLanguage() {
        assertEquals(0, SUDGILanguageConfig.getSelectedIndex());
        assertNull(com.blankj.utilcode.util.LanguageUtils.getAppliedLanguage());
        assertEquals(8, SUDGILanguageConfig.getLabels(application).length);
    }

    @Test
    public void allLanguageChoicesResolveActualWrapperResources() {
        String[] expected = {"确认", "Confirm", "Konfirmasi", "Confirmar", "ยืนยัน", "Xác nhận", "Подтвердить"};
        for (int index = 1; index <= expected.length; index++) {
            selectLanguage(index);
            assertEquals(index, SUDGILanguageConfig.getSelectedIndex());
            assertEquals(expected[index - 1], application.getString(R.string.sud_op_wck_common_confirm));
            assertEquals(Locale.getDefault(), application.getResources().getConfiguration().getLocales().get(0));
        }
    }

    @Test
    public void savedSelectionRestoresApplicationResourcesAfterConfigurationReset() {
        selectLanguage(1);
        Configuration reset = new Configuration(application.getResources().getConfiguration());
        reset.setLocale(Locale.ENGLISH);
        application.getResources().updateConfiguration(reset, application.getResources().getDisplayMetrics());
        Locale.setDefault(Locale.ENGLISH);

        SUDGILanguageConfig.restoreApplicationLanguage();

        assertEquals(1, SUDGILanguageConfig.getSelectedIndex());
        assertEquals("确认", application.getString(R.string.sud_op_wck_common_confirm));
        assertEquals(Locale.SIMPLIFIED_CHINESE, Locale.getDefault());
    }

    @Test
    public void systemChoiceClearsManualOverride() {
        Locale systemLocale = com.blankj.utilcode.util.LanguageUtils.getSystemLanguage();
        selectLanguage(1);
        selectLanguage(0);
        assertNull(com.blankj.utilcode.util.LanguageUtils.getAppliedLanguage());
        assertEquals(0, SUDGILanguageConfig.getSelectedIndex());
        assertEquals(systemLocale, application.getResources().getConfiguration().getLocales().get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeChoiceIsRejected() {
        SUDGILanguageConfig.selectLanguage(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void outOfRangeChoiceIsRejected() {
        SUDGILanguageConfig.selectLanguage(8);
    }

    @Test
    public void settingsPageRestoresChoiceAndWrapperDialogUsesIt() {
        selectLanguage(4);
        try (ActivityController<SUDGILanguageActivity> controller =
                     Robolectric.buildActivity(SUDGILanguageActivity.class).setup()) {
            SUDGILanguageActivity activity = controller.get();
            ListView languageList = activity.findViewById(R.id.sud_gi_language_list);
            assertEquals(4, languageList.getCheckedItemPosition());
            assertEquals(8, languageList.getCount());

            SUDOPShowModalParams params = new SUDOPShowModalParams();
            params.title = "Game supplied title";
            params.showCancel = true;
            params.cancelColor = "#303133";
            params.confirmColor = "#007aff";
            ModalDialog dialog = new ModalDialog(activity, params);
            dialog.show();
            assertEquals("Confirmar", ((TextView) dialog.findViewById(R.id.tv_confirm)).getText().toString());
            assertEquals("Cancelar", ((TextView) dialog.findViewById(R.id.tv_cancel)).getText().toString());
            assertEquals("Game supplied title", ((TextView) dialog.findViewById(R.id.tv_title)).getText().toString());
            dialog.dismiss();

            activity.findViewById(R.id.sud_gi_language_back).performClick();
            assertTrue(activity.isFinishing());
        }
    }

    @Test
    public void tappingLanguageInSettingsPersistsSelection() {
        try (ActivityController<SUDGILanguageActivity> controller =
                     Robolectric.buildActivity(SUDGILanguageActivity.class).setup()) {
            ListView languageList = controller.get().findViewById(R.id.sud_gi_language_list);
            languageList.performItemClick(null, 3, 3);
            assertEquals(3, SUDGILanguageConfig.getSelectedIndex());
            shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(32));
            assertEquals("Konfirmasi", application.getString(R.string.sud_op_wck_common_confirm));
        }
    }

    @Test
    public void russianSelectionRestoresResourcesAndFormatsErrorArguments() {
        selectLanguage(7);
        Configuration reset = new Configuration(application.getResources().getConfiguration());
        reset.setLocale(Locale.ENGLISH);
        application.getResources().updateConfiguration(reset, application.getResources().getDisplayMetrics());
        Locale.setDefault(Locale.ENGLISH);

        SUDGILanguageConfig.restoreApplicationLanguage();

        assertEquals(7, SUDGILanguageConfig.getSelectedIndex());
        assertEquals("ru", Locale.getDefault().getLanguage());
        assertEquals("Подтвердить", application.getString(R.string.sud_op_wck_common_confirm));
        assertEquals("Неподдерживаемый тип файла: webp", application.getString(R.string.sud_op_wck_error_unsupported_file_type, "webp"));
    }

    @Test
    public void russianSettingsSelectionUsesActualWrapperDialogResources() {
        try (ActivityController<SUDGILanguageActivity> controller =
                     Robolectric.buildActivity(SUDGILanguageActivity.class).setup()) {
            ListView languageList = controller.get().findViewById(R.id.sud_gi_language_list);
            assertEquals("русский", languageList.getItemAtPosition(7).toString());
            languageList.performItemClick(null, 7, 7);
            shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(32));
            assertEquals(7, SUDGILanguageConfig.getSelectedIndex());
        }

        try (ActivityController<SUDGILanguageActivity> controller =
                     Robolectric.buildActivity(SUDGILanguageActivity.class).setup()) {
            SUDGILanguageActivity activity = controller.get();
            ListView languageList = activity.findViewById(R.id.sud_gi_language_list);
            assertEquals(7, languageList.getCheckedItemPosition());

            SUDOPShowModalParams params = new SUDOPShowModalParams();
            params.title = "Game supplied title";
            params.showCancel = true;
            params.cancelColor = "#303133";
            params.confirmColor = "#007aff";
            ModalDialog dialog = new ModalDialog(activity, params);
            try {
                dialog.show();
                assertEquals("Подтвердить", ((TextView) dialog.findViewById(R.id.tv_confirm)).getText().toString());
                assertEquals("Отмена", ((TextView) dialog.findViewById(R.id.tv_cancel)).getText().toString());
                assertEquals("Game supplied title", ((TextView) dialog.findViewById(R.id.tv_title)).getText().toString());
            } finally {
                dialog.dismiss();
            }
        }
    }

    private void selectLanguage(int index) {
        SUDGILanguageConfig.selectLanguage(index);
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(32));
    }
}
