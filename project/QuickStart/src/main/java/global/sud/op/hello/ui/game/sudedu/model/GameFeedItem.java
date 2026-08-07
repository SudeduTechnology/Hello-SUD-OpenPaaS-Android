package global.sud.op.hello.ui.game.sudedu.model;

import com.google.gson.annotations.SerializedName;

import global.sud.op.hello.ui.main.GameModel;
import global.sud.op.runtime.core.model.SUDOPGamePathType;

public class GameFeedItem {

    @SerializedName(value = "gameId", alternate = {"game_id"})
    public String gameId;

    @SerializedName(value = "gameName", alternate = {"game_name"})
    public String gameName;

    @SerializedName(value = "description", alternate = {"gameDescription", "game_description"})
    public String description;

    @SerializedName(value = "gameVersion", alternate = {"game_version"})
    public String gameVersion;

    @SerializedName(value = "gameUrl", alternate = {"game_url", "downloadUrl"})
    public String gameUrl;

    @SerializedName(value = "gamePkgVersion", alternate = {"pkgVersion", "pkg_version"})
    public String gamePkgVersion;

    @SerializedName(value = "coverUrl", alternate = {"cover_url", "gameIcon", "game_icon"})
    public String coverUrl;

    @SerializedName(value = "codeVersion", alternate = {"code_version"})
    public String codeVersion;

    @SerializedName(value = "packageMd5", alternate = {"package_md5"})
    public String packageMd5;

    @SerializedName(value = "status", alternate = {"linkStatus", "link_status", "relationStatus", "relation_status"})
    public Integer status;

    public GameModel toGameModel() {
        GameModel model = new GameModel();
        model.gameId = gameId;
        model.gameName = gameName;
        model.gameUrl = gameUrl;
        model.gamePkgVersion = gamePkgVersion;
        model.pathType = SUDOPGamePathType.GAME_ID;
        return model;
    }

}
