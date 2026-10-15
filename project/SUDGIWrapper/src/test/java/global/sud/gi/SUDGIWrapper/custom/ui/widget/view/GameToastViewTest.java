package global.sud.gi.SUDGIWrapper.custom.ui.widget.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.text.StaticLayout;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34, manifest = Config.NONE)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class GameToastViewTest {

    private Context context;
    private GameToastView toastView;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        toastView = new GameToastView(context);
    }

    @Test
    public void iconModePreservesLongLatinTitleAndExpandsHeight() {
        assertLongTitleLayout("The image was saved successfully. Please check your photo album.");
    }

    @Test
    public void iconModePreservesLongThaiTitleAndExpandsHeight() {
        assertLongTitleLayout("บันทึกรูปภาพเรียบร้อยแล้ว กรุณาตรวจสอบอัลบั้มรูป");
    }

    @Test
    public void iconModePreservesLongVietnameseTitleAndExpandsHeight() {
        assertLongTitleLayout("Đã lưu hình ảnh thành công. Vui lòng kiểm tra thư viện ảnh.");
    }

    @Test
    public void iconModePreservesMixedChineseAndLatinTitleAndExpandsHeight() {
        assertLongTitleLayout("图片保存成功 Image saved successfully，请检查相册");
    }

    private void assertLongTitleLayout(String title) {
        float density = context.getResources().getDisplayMetrics().density;
        int viewWidth = Math.round(200 * density);
        GameToastView.IconToastLayout toastLayout = toastView.calculateIconToastLayout(title, viewWidth);
        StaticLayout titleLayout = toastLayout.titleLayout;

        assertNotNull(titleLayout);
        assertEquals(title, titleLayout.getText().toString());
        assertTrue("Expected long title to wrap onto multiple lines", titleLayout.getLineCount() > 1);
        for (int line = 0; line < titleLayout.getLineCount(); line++) {
            assertEquals("Title must not be ellipsized", 0, titleLayout.getEllipsisCount(line));
        }

        float expectedMaxWidth = Math.max(dp(1, density), Math.min(dp(320, density), viewWidth - dp(40, density)));
        assertTrue("Toast width must stay within the available viewport", toastLayout.boxWidth <= expectedMaxWidth + 0.5f);
        assertTrue("Long title must expand the Toast beyond its minimum height", toastLayout.boxHeight > dp(122, density));
        float expectedHeight = dp(21, density) + dp(44, density) + dp(12, density) + titleLayout.getHeight() + dp(18, density);
        assertEquals(expectedHeight, toastLayout.boxHeight, 0.5f);
    }

    private float dp(float value, float density) {
        return value * density + 0.5f;
    }
}
