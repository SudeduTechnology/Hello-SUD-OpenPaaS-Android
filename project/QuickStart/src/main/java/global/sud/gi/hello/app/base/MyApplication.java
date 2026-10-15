/*
 * Copyright © Sud.Tech
 * https://sud.tech
 */

package global.sud.gi.hello.app.base;

import android.app.Application;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.view.Gravity;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import global.sud.gi.core.ISUDAPPD;
import global.sud.gi.hello.BuildConfig;
import global.sud.gi.hello.app.common.utils.SUDGILanguageConfig;

public class MyApplication extends Application {

    public static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        SUDGILanguageConfig.restoreApplicationLanguage();
        String processName = getProcessName(android.os.Process.myPid());
        LogUtils.d("processName:" + processName);
        if (getPackageName().equals(processName)) {//只有是当前进程下才需要初始化如下参数
            instance = this;
//            ISUDAPPD.e(1); // 固定使用 Pro 环境。
            ISUDAPPD.e(3);
            configLog();
            configToast();
            initCrashHandler();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        SUDGILanguageConfig.restoreApplicationLanguage();
    }

    private void initCrashHandler() {
        CrashHandler instance = CrashHandler.getInstance();
        instance.init(this);
    }

    private void configLog() {
        LogUtils.Config config = LogUtils.getConfig();
        if (config != null) {
            config.setConsoleSwitch(BuildConfig.DEBUG);
            config.setSaveDays(2);
            config.setLogHeadSwitch(false);
            config.setSingleTagSwitch(true);
            config.setBorderSwitch(false);
        }
    }

    private void configToast() {
        ToastUtils.getDefaultMaker().setGravity(Gravity.CENTER, 0, 0);
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

}
