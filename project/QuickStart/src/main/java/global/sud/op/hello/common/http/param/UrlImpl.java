package global.sud.op.hello.common.http.param;

public class UrlImpl implements IBaseUrl {

    private String[] urlArr = {
            "https://hello-op.sud.ltd/",
            "https://hello-op.sud.ltd/",
            "https://hello-op.sud.ltd/",
            "https://hello-op.sud.ltd/"
    };

    private int env = 0;

    @Override
    public void setEnv(EnvType envType) {
        switch (envType) {
            case PRO:
                env = 0;
                break;
            case SIM:
                env = 1;
                break;
            case FAT:
                env = 2;
                break;
            case DEV:
                env = 3;
                break;
        }
    }

    @Override
    public String getBaseUrl() {
        return urlArr[env];
    }

}
