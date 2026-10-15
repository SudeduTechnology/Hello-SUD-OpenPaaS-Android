package global.sud.gi.hello.app.game.gi.data;

import java.io.Serializable;

public class GIGameModel implements Serializable {
    public String gameName; // 游戏名称
    public long gameId; // 游戏id
    public int homeGamePic; // 首页展示的游戏图标
    public int gamePic; // 游戏图标
}
