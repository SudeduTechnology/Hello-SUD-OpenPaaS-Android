package global.sud.op.hello.ui.game;

import global.sud.op.runtime.api.SUDRTJSCallback;
import global.sud.runtime.annotation.SUDASync;
import global.sud.runtime.annotation.SUDSync;

public class QgExtendedClient {
    private static final String TAG = "QgExtendedClient";

    public static String testLogin() {
        return ""
                + "(function() {  "
                + "  try {  "
                + "    sud.login({  "
                + "      userId: 'inject-test',  "
                + "      success: function(msg) {  "
                + "        if (typeof console !== 'undefined' && console.log) {  "
                + "          console.log('login success', msg);  "
                + "        }  "
                + "      },  "
                + "      fail: function(msg) {  "
                + "        if (typeof console !== 'undefined' && console.log) {  "
                + "          console.log('login fail', msg);  "
                + "        }  "
                + "      }  "
                + "    });  "
                + "  } catch (e) {  "
                + "      console.error(e);  "
                + "  }  "
                + "})();  ";
    }

    public static class LoginOptions extends CommonOptions {
        public String userId = "userID is null";
    }

    public static class CommonOptions {

        public SUDRTJSCallback successcb;
        public SUDRTJSCallback failcb;
        public SUDRTJSCallback completecb;

        @SUDASync
        public void success(SUDRTJSCallback cb) {
            if (this.successcb != null) {
                this.successcb.release();
            }
            this.successcb = cb;
        }

        @SUDASync
        public void fail(SUDRTJSCallback cb) {
            if (this.failcb != null) {
                this.failcb.release();
            }
            this.failcb = cb;
        }

        @SUDASync
        public void complete(SUDRTJSCallback cb) {
            if (this.completecb != null) {
                this.completecb.release();
            }
            this.completecb = cb;
        }
    }

    @SUDASync
    public void login(LoginOptions options) {
        if (options == null) {
            return;
        }
        SUDRTJSCallback successcb = options.successcb;
        SUDRTJSCallback failcb = options.failcb;
        SUDRTJSCallback completecb = options.completecb;
        if (successcb != null) {
            successcb.invoke("login success " + options.userId);
            successcb.release();
        }
        if (failcb != null) {
//            failcb.invoke("login failed" + options.userId);
            failcb.release();
        }
        if (completecb != null) {
            completecb.invoke();
            completecb.release();
        }
    }

    /**
     * 这是同步方法，请不要阻塞
     */
    @SUDSync
    public String getProvider() {
        return "provider";
    }

    @SUDASync
    public void onAuthDialogShow(SUDRTJSCallback callback) {
        int authType = 1;
        callback.invoke(authType);
    }

    @SUDASync
    public void offAuthDialogShow(SUDRTJSCallback callback) {
        callback.release();
    }

    @SUDASync
    public void onAuthDialogClose(SUDRTJSCallback callback) {
        int authType = 1;
        callback.invoke(authType);
    }

    @SUDASync
    public void offAuthDialogClose(SUDRTJSCallback callback) {
        callback.release();
    }

    @SUDASync
    public void installShortcut(CommonOptions options) {
        SUDRTJSCallback successcb = options.successcb;
        SUDRTJSCallback failcb = options.failcb;
        SUDRTJSCallback completecb = options.completecb;
        if (successcb != null) {
            successcb.invoke();
            successcb.release();
        }
        if (failcb != null) {
//            failcb.invoke(err);
            failcb.release();
        }
        if (completecb != null) {
            completecb.invoke();
            completecb.release();
        }
    }

    @SUDASync
    public void hasShortcutInstalled(CommonOptions options) {
        SUDRTJSCallback successcb = options.successcb;
        SUDRTJSCallback failcb = options.failcb;
        SUDRTJSCallback completecb = options.completecb;
        if (successcb != null) {
            successcb.invoke(); // custom params
            successcb.release();
        }
        if (failcb != null) {
//            failcb.invoke(err);
            failcb.release();
        }
        if (completecb != null) {
            completecb.invoke();
            completecb.release();
        }
    }

    @SUDASync
    public void isStartupByShortcut(CommonOptions options) {
        SUDRTJSCallback successcb = options.successcb;
        SUDRTJSCallback failcb = options.failcb;
        SUDRTJSCallback completecb = options.completecb;
        if (successcb != null) {
            successcb.invoke(); // custom params
            successcb.release();
        }
        if (failcb != null) {
//            failcb.invoke(err);
            failcb.release();
        }
        if (completecb != null) {
            completecb.invoke();
            completecb.release();
        }
    }

}
