package global.sud.gi.hello.app.base;

public class AppConfig {
    /** false 使用 FAT 环境，true 使用正式环境。 */
    private static final boolean USE_PRODUCTION = false;

    private static final String FAT_OP_APP_ID = "2071435303700652034";
    private static final String FAT_OP_APP_KEY = "P9vaelwdulzfKoZFmaQAICVFmEsbRAlm";
    private static final String FAT_OP_SELECTION_ID = "2109958437529444353";

    // TODO: 获取正式环境配置后，替换以下四项当前临时复用的 FAT 环境值。
    private static final String PRODUCTION_OP_APP_ID = "2071435303700652034";
    private static final String PRODUCTION_OP_APP_KEY = "P9vaelwdulzfKoZFmaQAICVFmEsbRAlm";
    private static final String PRODUCTION_OP_SELECTION_ID = "2109958437529444353";

    public static final String OP_APP_ID = USE_PRODUCTION ? PRODUCTION_OP_APP_ID : FAT_OP_APP_ID;
    public static final String OP_APP_KEY = USE_PRODUCTION ? PRODUCTION_OP_APP_KEY : FAT_OP_APP_KEY;
    public static final String OP_SELECTION_ID = USE_PRODUCTION
            ? PRODUCTION_OP_SELECTION_ID
            : FAT_OP_SELECTION_ID;
    public static String APP_BASE_URL = "https://app.sudedu.com:17888";

    /** GI QuickStart 专用配置，与 OpenPaaS 配置相互独立。 */
    public static final String GI_APP_ID = "1461564080052506636";
    public static final String GI_APP_KEY = "03pNxK2lEXsKiiwrBQ9GbH541Fk2Sfnc";
    public static final String GI_LOGIN_URL = "https://prod-hellosud-base.s00.tech/login/v3";
    public static final boolean GI_GAME_IS_TEST_ENV = false;
    public static final String GI_DEFAULT_ROOM_ID = "10000";

}
