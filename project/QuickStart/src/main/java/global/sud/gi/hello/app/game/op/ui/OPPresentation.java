package global.sud.gi.hello.app.game.op.ui;

import java.util.Map;

import global.sud.op.runtime.core.model.SUDOPGameListItem;

public final class OPPresentation {

    private OPPresentation() {
    }

    public static String resolveName(SUDOPGameListItem item, String languageTag) {
        if (item == null) {
            return "";
        }
        String name = resolveMapValue(item.gameName, languageTag);
        return isEmpty(name) ? item.gameId : name;
    }

    public static String resolveIcon(SUDOPGameListItem item, String languageTag) {
        if (item == null) {
            return "";
        }
        String icon = resolveMapValue(item.icon, languageTag);
        return isEmpty(icon) ? safe(item.gameIcon) : icon;
    }

    static String resolveMapValue(Map<String, String> values, String languageTag) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        if (!isEmpty(languageTag)) {
            String exact = values.get(languageTag);
            if (!isEmpty(exact)) {
                return exact;
            }
        }
        String defaultValue = values.get("default");
        if (!isEmpty(defaultValue)) {
            return defaultValue;
        }
        for (String value : values.values()) {
            if (!isEmpty(value)) {
                return value;
            }
        }
        return "";
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    private static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }
}
