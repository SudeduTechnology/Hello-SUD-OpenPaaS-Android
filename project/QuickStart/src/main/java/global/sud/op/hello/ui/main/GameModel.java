package global.sud.op.hello.ui.main;

import java.io.Serializable;

import global.sud.op.runtime.core.model.SUDOPGamePathType;

public class GameModel implements Serializable {
    public static final int ORIENTATION_PORTRAIT = 1;
    public static final int ORIENTATION_LANDSCAPE = 2;

    public String gameName; // 游戏名称
    public String gameId; // 游戏id
    public String gameUrl; // 游戏Url
    public String gamePkgVersion; // 游戏包版本
    public int homeGamePic; // 首页展示的游戏图标
    public int gamePic; // 游戏图标
    public SUDOPGamePathType pathType;
    public String manifestJson; // 自定义的manifestJson内容
    public int orientationMode = ORIENTATION_PORTRAIT;
}
