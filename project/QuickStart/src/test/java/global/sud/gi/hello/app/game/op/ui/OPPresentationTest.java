package global.sud.gi.hello.app.game.op.ui;

import org.junit.Test;

import java.util.LinkedHashMap;

import global.sud.op.runtime.core.model.SUDOPGameListItem;

import static org.junit.Assert.assertEquals;

public class OPPresentationTest {

    @Test
    public void nameUsesLanguageThenDefaultThenGameId() {
        SUDOPGameListItem item = new SUDOPGameListItem();
        item.gameId = "game-1";
        item.gameName = new LinkedHashMap<>();
        item.gameName.put("default", "Default Name");
        item.gameName.put("zh-CN", "中文名");

        assertEquals("中文名", OPPresentation.resolveName(item, "zh-CN"));
        assertEquals("Default Name", OPPresentation.resolveName(item, "en-US"));

        item.gameName.clear();
        assertEquals("game-1", OPPresentation.resolveName(item, "en-US"));
    }

    @Test
    public void iconFallsBackToLegacyGameIcon() {
        SUDOPGameListItem item = new SUDOPGameListItem();
        item.icon = new LinkedHashMap<>();
        item.gameIcon = "https://example.com/legacy.png";

        assertEquals("https://example.com/legacy.png",
                OPPresentation.resolveIcon(item, "zh-CN"));
    }
}
