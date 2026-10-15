package global.sud.gi.hello.app.common.utils;

import android.content.Context;

import java.util.Locale;

import global.sud.gi.hello.R;

/** Demo 的应用语言；语言列表与 Wrapper 已提供的资源保持一致。 */
public final class SUDGILanguageConfig {

    private static final String[] LANGUAGE_TAGS = {"", "zh-CN", "en", "id", "pt", "th", "vi", "ru"};

    private static final String KEY_CUSTOM_LANGUAGE = "sud_gi_custom_language";
    private static final String KEY_CUSTOM_SELECTED = "sud_gi_custom_language_selected";

    private SUDGILanguageConfig() {
    }

    public static String[] getLabels(Context context) {
        String[] labels = new String[LANGUAGE_TAGS.length];
        labels[0] = context.getString(R.string.sud_gi_language_system);
        for (int index = 1; index < LANGUAGE_TAGS.length; index++) {
            Locale locale = Locale.forLanguageTag(LANGUAGE_TAGS[index]);
            labels[index] = locale.getDisplayLanguage(locale);
        }
        return labels;
    }

    public static int getSelectedIndex() {
        Locale applied = com.blankj.utilcode.util.LanguageUtils.getAppliedLanguage();
        if (applied != null) {
            if (GlobalSP.getSP().getBoolean(KEY_CUSTOM_SELECTED, false)) {
                return -1;
            }
            for (int index = 1; index < LANGUAGE_TAGS.length; index++) {
                if (Locale.forLanguageTag(LANGUAGE_TAGS[index]).equals(applied)) {
                    return index;
                }
            }
        }
        return 0;
    }

    public static String getCustomLanguageTag() {
        return GlobalSP.getSP().getString(KEY_CUSTOM_LANGUAGE, "");
    }

    public static String getSelectedLabel(Context context) {
        int index = getSelectedIndex();
        return index < 0 ? getCustomLanguageTag() : getLabels(context)[index];
    }

    public static void selectCustomLanguage(String languageTag) {
        String tag = languageTag == null ? "" : languageTag.trim();
        if (tag.isEmpty()) {
            throw new IllegalArgumentException("Language must not be empty");
        }
        Locale locale = new Locale.Builder().setLanguageTag(tag).build();
        if (locale.getLanguage().isEmpty()) {
            throw new IllegalArgumentException("Language tag must contain a language");
        }
        GlobalSP.getSP().put(KEY_CUSTOM_LANGUAGE, tag, true);
        GlobalSP.getSP().put(KEY_CUSTOM_SELECTED, true, true);
        applyLocale(locale);
    }

    public static void selectLanguage(int index) {
        if (index < 0 || index >= LANGUAGE_TAGS.length) {
            throw new IllegalArgumentException("Unsupported language index: " + index);
        }
        if (index == getSelectedIndex()) {
            return;
        }
        GlobalSP.getSP().put(KEY_CUSTOM_SELECTED, false, true);
        // 已有工具负责持久化选择、更新 Application 资源并重建页面。
        if (index == 0) {
            Locale.setDefault(com.blankj.utilcode.util.LanguageUtils.getSystemLanguage());
            com.blankj.utilcode.util.LanguageUtils.applySystemLanguage();
        } else {
            applyLocale(Locale.forLanguageTag(LANGUAGE_TAGS[index]));
        }
    }

    private static void applyLocale(Locale locale) {
        Locale.setDefault(locale);
        com.blankj.utilcode.util.LanguageUtils.applyLanguage(locale);
    }

    public static void restoreApplicationLanguage() {
        Locale locale = com.blankj.utilcode.util.LanguageUtils.getAppliedLanguage();
        if (locale == null) {
            locale = com.blankj.utilcode.util.LanguageUtils.getSystemLanguage();
        }
        // Wrapper 也通过 Utils.getApp() 读取资源，不能只包装 Activity 的 Context。
        Locale.setDefault(locale);
        com.blankj.utilcode.util.LanguageUtils.updateAppContextLanguage(locale, null);
    }
}
