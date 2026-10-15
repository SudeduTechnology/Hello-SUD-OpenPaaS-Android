package global.sud.gi.hello.app.common.utils;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import java.util.Locale;
import java.util.Map;

public class LanguageUtils {

    public static String getBCP47LanguageTag(Context context) {
        Locale locale = getLocale(context);
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return locale.toLanguageTag();
    }

    public static String getLanguageTag(Context context) {
        return localeToLanguageTag(getLocale(context));
    }

    public static Locale getLocale(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return context.getResources().getConfiguration().getLocales().get(0);
            }
            return context.getResources().getConfiguration().locale;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String localeToLanguageTag(Locale locale) {
        if (locale == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        String language = localeToLanguageTagNoCountry(locale);
        sb.append(language);
        String country = locale.getCountry();
        if (TextUtils.isEmpty(country)) {
            switch (language) {
                case "zh": // 中文
                    sb.append("-");
                    sb.append("CN");
                    break;
                case "en": // 英语
                    sb.append("-");
                    sb.append("US");
                    break;
                case "ar": // 阿拉伯语
                    sb.append("-");
                    sb.append("SA");
                    break;
                case "id": //印尼语
                case "in": //印尼语
                    sb.append("-");
                    sb.append("ID");
                    break;
                case "ms": // 马来
                    sb.append("-");
                    sb.append("MY");
                    break;
                case "th": // 泰国
                    sb.append("-");
                    sb.append("TH");
                    break;
                case "vi": // 越南
                    sb.append("-");
                    sb.append("VN");
                    break;
                case "ko": //韩国
                    sb.append("-");
                    sb.append("KR");
                    break;
                case "es": // 西班牙
                    sb.append("-");
                    sb.append("ES");
                    break;
                case "ja": //日本
                    sb.append("-");
                    sb.append("JP");
                    break;
            }
        } else {
            sb.append("-");
            sb.append(country);
        }
        return sb.toString();
    }

    public static String localeToLanguageTagNoCountry(Locale locale) {
        if (locale == null) {
            return "";
        }
        String language = locale.getLanguage();
        if (language.length() > 0) {
            if ("in".equals(language)) { // 印尼语，因后端只识别新的IOS编码,所以这里进行转换
                language = "id";
            }
        }
        return language;
    }

    public static String getMultiLanguageText(Context context, Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }

        String languageTag = getLanguageTag(context);
        if (!TextUtils.isEmpty(languageTag) && map.containsKey(languageTag)) {
            return map.get(languageTag);
        }

        if (map.containsKey("default")) {
            return map.get("default");
        }

        for (String value : map.values()) {
            return value;
        }

        return "";
    }

}
