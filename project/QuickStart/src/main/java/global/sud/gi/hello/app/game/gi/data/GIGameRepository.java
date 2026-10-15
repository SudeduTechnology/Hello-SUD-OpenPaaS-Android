package global.sud.gi.hello.app.game.gi.data;

import java.util.ArrayList;
import java.util.List;

import global.sud.gi.hello.R;

public class GIGameRepository {

    /** 获取游戏列表 */
    public static List<GIGameModel> getGameList() {
        ArrayList<GIGameModel> list = new ArrayList<>();
        list.add(buildGIGameModel(1461227817776713818L, "碰碰我最强", R.drawable.gi_home_ppwzq, R.drawable.gi_icon_ppwzq));
        list.add(buildGIGameModel(1461228379255603251L, "飞镖达人", R.drawable.gi_home_fbdr, R.drawable.gi_icon_fbdr));
        list.add(buildGIGameModel(1461228410184400899L, "你画我猜", R.drawable.gi_home_nhwc, R.drawable.gi_icon_nhwc));
        list.add(buildGIGameModel(1461297734886621238L, "五子棋", R.drawable.gi_home_wzq, R.drawable.gi_icon_wzq));
        list.add(buildGIGameModel(1461297789198663710L, "黑白棋", R.drawable.gi_home_hbq, R.drawable.gi_icon_hbq));
        list.add(buildGIGameModel(1468090257126719572L, "短道速滑", R.drawable.gi_home_ddsh, R.drawable.gi_icon_ddsh));
        list.add(buildGIGameModel(1468091457989509190L, "数字炸弹", R.drawable.gi_home_szzd, R.drawable.gi_icon_szzd));
        list.add(buildGIGameModel(1468180338417074177L, "飞行棋", R.drawable.gi_home_fxq, R.drawable.gi_icon_fxq));
        list.add(buildGIGameModel(1468434401847222273L, "扫雷", R.drawable.gi_home_sl, R.drawable.gi_icon_sl));
        list.add(buildGIGameModel(1468434504892882946L, "你说我猜", R.drawable.gi_home_nswc, R.drawable.gi_icon_nswc));
        list.add(buildGIGameModel(1468434637562912769L, "数字转轮", R.drawable.gi_home_szzl, R.drawable.gi_icon_szzl));
        list.add(buildGIGameModel(1468434723902660610L, "石头剪刀布", R.drawable.gi_home_stjdb, R.drawable.gi_icon_stjdb));
        list.add(buildGIGameModel(1557194487352053761L, "TeenPatti", R.drawable.gi_home_teen_patti, R.drawable.gi_icon_teen_patti));
        list.add(buildGIGameModel(1472142559912517633L, "UMO", R.drawable.gi_home_umo, R.drawable.gi_icon_umo));
        list.add(buildGIGameModel(1472142640866779138L, "排雷兵", R.drawable.gi_home_plb, R.drawable.gi_icon_plb));
        list.add(buildGIGameModel(1472142695162044417L, "台湾麻将", R.drawable.gi_home_twmj, R.drawable.gi_icon_twmj));
        list.add(buildGIGameModel(1472142747708284929L, "狼人杀", R.drawable.gi_home_lrs, R.drawable.gi_icon_lrs));
        list.add(buildGIGameModel(1490944230389182466L, "友尽闯关", R.drawable.gi_home_picopark, R.drawable.gi_icon_picopark));
        list.add(buildGIGameModel(1490944604005199873L, "大话骰", R.drawable.gi_home_dice, R.drawable.gi_icon_dice));
        list.add(buildGIGameModel(1557194155570024449L, "德州扑克", R.drawable.gi_home_dz, R.drawable.gi_icon_dz));
        list.add(buildGIGameModel(1582551621189419010L, "桌球", R.drawable.gi_home_zq, R.drawable.gi_icon_zq));
        list.add(buildGIGameModel(1572529757165293569L, "连线激斗", R.drawable.gi_home_lxjd, R.drawable.gi_icon_lxjd));
        list.add(buildGIGameModel(1559030313895714818L, "麻将对对碰", R.drawable.gi_home_mjddp, R.drawable.gi_icon_mjddp));
        list.add(buildGIGameModel(1564814666005299201L, "科学计算器", R.drawable.gi_home_kxjsq, R.drawable.gi_icon_kxjsq));
        list.add(buildGIGameModel(1564814818015264770L, "疯狂购物", R.drawable.gi_home_fkgw, R.drawable.gi_icon_fkgw));
        list.add(buildGIGameModel(1585556944972623874L, "魔法大乐斗", R.drawable.gi_home_mfdld, R.drawable.gi_icon_mfdld));
        return list;
    }

    /** 构建GIGameModel */
    public static GIGameModel buildGIGameModel(long gameId, String gameName, int homeGamePic, int gamePic) {
        GIGameModel model = new GIGameModel();
        model.gameId = gameId;
        model.gameName = gameName;
        model.homeGamePic = homeGamePic;
        model.gamePic = gamePic;
        return model;
    }

}
