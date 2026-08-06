package global.sud.op.hello.common.http.param;

public class BaseUrlManager {

    private static final IBaseUrl iBaseUrl = new UrlImpl();

    /**
     * 获取基础服务
     */
    public static String getBaseUrl() {
        return iBaseUrl.getBaseUrl();
    }

    public static void setEnv(IBaseUrl.EnvType envType) {
        iBaseUrl.setEnv(envType);
    }

}
