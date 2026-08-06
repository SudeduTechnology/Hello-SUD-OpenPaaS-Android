package global.sud.op.hello.common.utils;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.UiModeManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.input.InputManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.InputDevice;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;

import java.util.Locale;

/**
 * 系统相关工具类
 */
public class SystemUtils {
    private static int mIsTV = -1;

    /** 获取系统版本号 */
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /** 获取设备品牌 */
    public static String getDeviceBrand() {
        return Build.BRAND;
    }

    /** 获取设备id */
    public static String getDeviceId() {
        return DeviceUtils.getUniqueDeviceId();
    }

    /** 获取完整语言代码 */
    public static String getLanguageCode(Context context) {
        return localeToLanguageCode(getLocale(context));
    }

    /** 获取语言代码，不带地区 */
    public static String getLanguageCodeNoCountry(Context context) {
        return localeToLanguageCodeNoCountry(getLocale(context));
    }

    /** 获取locale */
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

    /** locale对象转换语言码 */
    public static String localeToLanguageCode(Locale locale) {
        if (locale == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        String language = localeToLanguageCodeNoCountry(locale);
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

    /** locale对象转换成只有一个语言编码，不带地区 */
    public static String localeToLanguageCodeNoCountry(Locale locale) {
        if (locale == null)
            return "";
        String language = locale.getLanguage();
        if (language.length() > 0) {
            if ("in".equals(language)) { // 印尼语，因后端只识别新的IOS编码,所以这里进行转换
                language = "id";
            }
        }
        return language;
    }

    /**
     * 获取{versionName.versionCode}
     */
    public static String getAppVersion() {
        return AppUtils.getAppVersionName() + "." + AppUtils.getAppVersionCode();
    }

    /** 获取版本名称 */
    public static String getVersionName() {
        return AppUtils.getAppVersionName();
    }

    /** 获取版本code */
    public static int getVersionCode() {
        return AppUtils.getAppVersionCode();
    }

    /** 是否是TV */
    public static Boolean isTV(Context context){
        // 测试代码
//        return false;

        if(mIsTV != -1){
            return mIsTV == 1;
        }
        do {
            PackageManager pm = context.getPackageManager();

            // 方法1：检查是否声明为电视设备
            if (pm.hasSystemFeature(PackageManager.FEATURE_TELEVISION) ||
                    pm.hasSystemFeature(PackageManager.FEATURE_LEANBACK)) {
                mIsTV = 1;
                break;
            }

            UiModeManager uiModeManager = (UiModeManager)context.getSystemService(Context.UI_MODE_SERVICE);
            if(uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION){
                mIsTV = 1;
                break;
            }

            // 方法2：检查是否存在电话功能
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null && tm.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
                // 无电话模块可能是电视或平板
            } else {
                mIsTV = 0;
                break; // 明确具备通话功能，倾向为手机
            }

            // 方法3：检测输入方式 - 是否主要依赖DPAD
            InputManager inputManager = (InputManager) context.getSystemService(Context.INPUT_SERVICE);
            int[] inputDeviceIds = inputManager.getInputDeviceIds();
            boolean hasDpad = false, hasTouchScreen = false;
            for (int id : inputDeviceIds) {
                InputDevice device = inputManager.getInputDevice(id);
                if ((device.getSources() & InputDevice.SOURCE_DPAD) == InputDevice.SOURCE_DPAD) {
                    hasDpad = true;
                }
                if ((device.getSources() & InputDevice.SOURCE_TOUCHSCREEN) == InputDevice.SOURCE_TOUCHSCREEN) {
                    hasTouchScreen = true;
                }
            }
            if (hasDpad && !hasTouchScreen){
                mIsTV = 1;
                break;
            }

            // 方法4：Build信息辅助判断
            String manufacturer = Build.MANUFACTURER.toLowerCase();
            String model = Build.MODEL.toLowerCase();
            if (manufacturer.contains("xiaomi") && model.contains("tv")) {
                mIsTV = 1;
                break;
            }
            if (manufacturer.contains("hisense") || manufacturer.contains("skyworth")) {
                mIsTV = 1;
                break;
            }
        }while (false);
        return mIsTV == 1;
    }

}
