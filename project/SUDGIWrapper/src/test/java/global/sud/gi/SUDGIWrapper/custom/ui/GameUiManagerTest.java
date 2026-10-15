package global.sud.gi.SUDGIWrapper.custom.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import global.sud.gi.SUDGIWrapper.custom.ui.widget.view.GameToastView;

public class GameUiManagerTest {

    private static final String DEFAULT_LOADING_TITLE = "Loading…";
    private static final String DEFAULT_SUCCESS_TITLE = "Success";

    @Test
    public void loadingTitleUsesDefaultOnlyForNullOrEmptyInput() {
        assertEquals(DEFAULT_LOADING_TITLE, GameUiManager.resolveLoadingTitle(null, DEFAULT_LOADING_TITLE));
        assertEquals(DEFAULT_LOADING_TITLE, GameUiManager.resolveLoadingTitle("", DEFAULT_LOADING_TITLE));
        assertEquals("Custom loading title", GameUiManager.resolveLoadingTitle("Custom loading title", DEFAULT_LOADING_TITLE));
        assertEquals(" ", GameUiManager.resolveLoadingTitle(" ", DEFAULT_LOADING_TITLE));
    }

    @Test
    public void successToastUsesDefaultOnlyForNullOrEmptyInput() {
        assertEquals(DEFAULT_SUCCESS_TITLE, GameUiManager.resolveToastTitle(null, GameToastView.ICON_SUCCESS, DEFAULT_SUCCESS_TITLE));
        assertEquals(DEFAULT_SUCCESS_TITLE, GameUiManager.resolveToastTitle("", GameToastView.ICON_SUCCESS, DEFAULT_SUCCESS_TITLE));
        assertEquals("Saved by the game", GameUiManager.resolveToastTitle("Saved by the game", GameToastView.ICON_SUCCESS, DEFAULT_SUCCESS_TITLE));
        assertEquals(" ", GameUiManager.resolveToastTitle(" ", GameToastView.ICON_SUCCESS, DEFAULT_SUCCESS_TITLE));
    }

    @Test
    public void nonSuccessToastDoesNotInventADefaultTitle() {
        assertNull(GameUiManager.resolveToastTitle(null, GameToastView.ICON_ERROR, DEFAULT_SUCCESS_TITLE));
        assertEquals("", GameUiManager.resolveToastTitle("", GameToastView.ICON_LOADING, DEFAULT_SUCCESS_TITLE));
        assertEquals("Dynamic error", GameUiManager.resolveToastTitle("Dynamic error", GameToastView.ICON_ERROR, DEFAULT_SUCCESS_TITLE));
    }
}
