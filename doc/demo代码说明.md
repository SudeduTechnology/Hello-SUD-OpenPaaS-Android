# Demo 代码说明

## 模块结构

| 模块 | 说明 |
| --- | --- |
| `QuickStart` | 主应用模块，包名 `global.sud.op.hello`，包含 App、业务逻辑（service）、UI 及各游戏房间实现 |
| `SUD-OP-SDK` | SUD 开放平台 SDK 模块（含 `.aar` 静态库与 SUDGI 接口） |
| `SUDRuntimeWrapper` | SUD 运行时包装模块 |
| `storagechooser` | 第三方存储目录选择器库 |

## 关键配置

> [!NOTE]
> 以下配置均来自 demo 自带配置，可先直接使用体验，后续再申请自己的账号。

- **AppConfig.java**：应用配置文件，包含应用 ID、应用密钥等
- **SudGIP_AES_KEY_B64**：仅用于演示生成 `code`，实际应用中应存放在服务器端，由服务器生成 `code`
- **TEST_GAME_ID**：小游戏的 `gameid`，推荐使用`gameid`的方式打开小游戏
- **TEST_GAME_URL**：本地部署的小游戏的 `url`，方便快速测试

## 代码导航

| 模块 | 路径 |
| --- | --- |
| 应用入口 | `QuickStart/src/main/java/global/sud/op/hello/app/HelloSudApplication.java` |
| 工具类 | `QuickStart/src/main/java/global/sud/op/hello/QuickStartUtils.java` |
| UI 层 | `QuickStart/src/main/java/global/sud/op/hello/ui/` |
| 小游戏 Activity | `QuickStart/src/main/java/global/sud/op/hello/ui/game/QuickStartGameActivity.java` |
| 小游戏 ViewModel | `QuickStart/src/main/java/global/sud/op/hello/ui/game/QuickStartGameViewModel.java` |
| 小游戏与原生端自定义交互接口 | `QuickStart/src/main/java/global/sud/op/hello/ui/game/sudedu/SeduExtendedClient.java` |

## 可测试的小游戏列表

| 小游戏 | GameId |
| --- | --- |
| 小伴龙-恐龙世界 | `2071499456960258050` |
| 小伴龙-西游记-宝象国 | `2071525134501928962` |
| 奇峰AI-数学 | `2071791545870704641` |
| 英语单词PK | `2077946171923288065` |
