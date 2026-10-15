[English](./README.md)

# Hello-SUD-OpenPaaS-Android

本工程同时提供两套彼此独立的 Android 游戏接入示例：

- **OP（OpenPaaS）**：通过 `SUDOP` 获取平台游戏列表、完成用户鉴权并启动游戏。
- **GI（Game Interaction）**：通过 `SUDGI` 按 `gameId + roomId` 加载互动小游戏，并处理 App 与游戏之间的状态交互。

两套流程共用 `SUDGIWrapper` 中的部分宿主能力，但使用不同的入口页面、配置、鉴权凭证和生命周期 API。接入业务工程前请先确认目标方案，不要混用 OP 的 `userSignature` 与 GI 的短期 `code`。

## OP 与 GI 的区别

| 对比项 | OP（OpenPaaS） | GI（Game Interaction） |
| --- | --- | --- |
| SDK 入口 | `global.sud.op.runtime.core.SUDOP` | `global.sud.gi.core.SUDGI` |
| Demo 入口 | `OPListActivity.start(context)` | `GIQuickStartActivity.start(context)` |
| 游戏列表 | `SUDOP.getGameList(...)` 分页获取 | `GIGameRepository` 本地演示列表 |
| 用户凭证 | 业务服务端生成 `userSignature` | 业务服务端签发短期 `code` |
| 启动参数 | `gameId`、语言 | `gameId`、`roomId`、语言、`code` |
| 启动 API | `SUDOP.startGame(...)` | `SUDGI.loadMG(...)` |
| 生命周期 | `start / play / pause / stop / destroy` | `playMG / pauseMG / destroyMG` |
| 状态交互 | `SUDOPWrappedClient` 承接宿主能力 | `GIFSTAPPDecorator` 与 `GIFSMMGDecorator` 双向交互 |

## 环境要求

- Android Studio Narwhal Feature Drop（2025.1.2，与当前 AGP 8.12.0 配套）或兼容 AGP 8.12.0 的版本
- JDK 17
- Android Gradle Plugin 8.12.0
- Gradle 8.13（使用工程内置 Wrapper）
- compileSdk 36、targetSdk 36、minSdk 26
- Java 8 源码兼容级别

SDK 版本统一定义在 `project/SUDGIWrapper/build.gradle`，当前为 `2.1.0`。

## 快速运行 Demo

1. 使用 Android Studio 打开 `project/` 目录，而不是仓库根目录。
2. 等待 Gradle 同步，并确认 Android SDK 36 与 JDK 17 已配置。
3. 选择 `QuickStart` 模块和 `officialDebug` 变体。
4. 运行到 Android 8.0（API 26）或更高版本的设备/模拟器。
5. 在首页分别点击 **OpenPaaS 游戏** 或 **GI 游戏**。

命令行构建：

```shell
cd project
./gradlew :QuickStart:assembleOfficialDebug
```

Debug APK 输出目录：

```text
project/QuickStart/build/outputs/apk/official/debug/
```

## 工程结构

```text
project/
├── QuickStart/                         # Demo 应用
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/global/sud/gi/hello/app/
│       │   ├── base/AppConfig.java     # OP 与 GI 演示配置
│       │   ├── game/op/                # OP 完整流程
│       │   ├── game/gi/                # GI 完整流程
│       │   └── ui/main/                # 首页与语言设置
│       └── res/                        # 页面、图片和多语言资源
└── SUDGIWrapper/                       # SDK 依赖与公共封装
    └── src/main/java/global/sud/gi/SUDGIWrapper/
        ├── base/                       # OP 游戏生命周期封装
        ├── custom/                     # 图片、视频、弹窗等宿主能力
        └── gi/                         # GI 状态机、模型和装饰器
```

应用首页为 `global.sud.gi.hello.app.ui.main.MainActivity`。首页只负责跳转，OP 与 GI 的业务代码不会互相调用。

## 公共模块：SUDGIWrapper

通过 Android Studio 导入 `project/SUDGIWrapper`，或在已有 Gradle 工程中声明模块后添加依赖：

```groovy
dependencies {
    implementation project(':SUDGIWrapper')
}
```

该模块包含：

- SUDGI/SUDOP SDK 与广告适配器依赖；
- OP 使用的 `BaseGameViewModel`；
- GI 使用的状态机装饰器、数据模型及工具类；

如需上架 Google Play，请先按业务需要检查 `project/SUDGIWrapper/build.gradle` 中的 SDK 类型、广告源与隐私合规配置，不要直接照搬 Demo 的全部广告依赖。

# OP（OpenPaaS）接入

## 1. 适用场景

OP 示例适合以下流程：

- 从 OpenPaaS 获取当前应用或选品下的游戏列表；
- 使用业务自己的账号体系登录；
- 从业务服务端获取 `userSignature`；
- 调用`SUDOP.initSDK` 初始化SDK；
- 调用 `SUDOP.auth` 同步用户登录态；
- 调用 `SUDOP.startGame` 启动指定的游戏；

## 2. 关键代码与资源

```text
project/QuickStart/src/main/java/global/sud/gi/hello/app/game/op/
├── OPListActivity.java                 # SDK 初始化、账号准备、游戏列表与重试
├── OPActivity.java                     # 游戏页面、View 挂载与生命周期
├── OPViewModel.java                    # userSignature 获取与启动参数
├── OPWrappedClient.java                # 游戏调用 App 的宿主能力
├── data/                               # 本地会话模型与存储
├── net/                                # 登录和 userSignature 网络请求
├── repository/                         # 游戏列表分页、去重与取消
└── ui/                                 # 列表展示与多语言字段回退
```

对应布局：

```text
project/QuickStart/src/main/res/layout/activity_op_list.xml
project/QuickStart/src/main/res/layout/activity_op.xml
project/QuickStart/src/main/res/layout/item_op.xml
```

## 3. 配置 OP 参数

在 `project/QuickStart/src/main/java/global/sud/gi/hello/app/base/AppConfig.java` 中配置：

```java
public static final String OP_APP_ID = "YOUR_OP_APP_ID";
public static final String OP_APP_KEY = "YOUR_OP_APP_KEY";
public static final String OP_SELECTION_ID = "YOUR_SELECTION_ID";
public static String APP_BASE_URL = "https://your-app-server.example.com";
```

- `OP_APP_ID`、`OP_APP_KEY`：初始化 SUDOP SDK，必须与控制台中的 Android 应用一致。
- `OP_SELECTION_ID`：选品 ID；传空字符串时调用不带选品 ID 的游戏列表接口。
- `APP_BASE_URL`：自己的业务服务端地址。
- Demo 参数仅用于示例；正式接入前必须替换，并在控制台登记正式 `applicationId`。

## 4. 对接业务服务端

当前 `OPApiClient` 演示两个接口：

```text
POST /api/auth/login
POST /api/openpaas/user-signature
```

`/api/auth/login` App自己的游客登录接口。使用空 JSON 请求体，并通过 `Idempotency-Key` 避免重复创建游客用户。客户端保存服务端返回的 App Token、有效期与用户资料；本地会话失效时重新登录。

请求：

```http
POST /api/auth/login HTTP/1.1
Content-Type: application/json
Idempotency-Key: <8～200 个字符的稳定随机键>

{}
```

成功返回（`200 OK`，`appTokenExpiresIn` 单位为秒）：

```json
{
  "appToken": "<不透明的 App Token>",
  "appTokenExpiresIn": 2592000,
  "uid": 10000,
  "nickname": "小星星",
  "avatarUrl": "https://cdn.example.com/avatars/10000/avatar.png",
  "casdoorBound": false
}
```

`/api/openpaas/user-signature` 获取OP SDK认证所需的userSignature。使用 `Authorization: Bearer <App Token>`。服务端返回的 `appId`、用户 ID 和 `userSignature` 必须与当前会话一致。收到 401 时，Demo 会清理会话、重新登录并只重试一次。

请求：

```http
POST /api/openpaas/user-signature HTTP/1.1
Authorization: Bearer <不透明的 App Token>
Content-Type: application/json

{}
```

成功返回（`200 OK`，`expiresAt` 为 ISO 8601 时间）：

```json
{
  "appId": "YOUR_OP_APP_ID",
  "user": {
    "id": 10000,
    "name": "小星星",
    "avatar": "https://cdn.example.com/avatars/10000/avatar.png"
  },
  "userSignature": "<短期 userSignature>",
  "expiresAt": "2026-10-15T12:00:00.000Z"
}
```

两个接口的非 2xx 返回使用相同错误结构：

```json
{
  "error": {
    "code": "APP_TOKEN_INVALID_OR_EXPIRED",
    "message": "App Token 无效或已过期",
    "requestId": "<请求 ID>"
  }
}
```

生产环境必须遵守以下边界：

- `userSignature` 由业务服务端生成，Android 客户端只负责请求和使用；
- 数据对称密钥、应用私钥、App Secret 等服务端密钥不得放入 APK；
- 不通过 Intent、URL、日志或埋点传递 App Token、`userSignature` 和服务端密钥；
- `userSignature` 应设置合理有效期，过期后向服务端重新申请。

## 5. 注册页面与准备容器

在宿主应用的 `AndroidManifest.xml` 中注册：

```xml
<activity
    android:name="global.sud.gi.hello.app.game.op.OPListActivity"
    android:exported="false"
    android:screenOrientation="portrait" />

<activity
    android:name="global.sud.gi.hello.app.game.op.OPActivity"
    android:configChanges="locale|orientation|screenSize|keyboardHidden|navigation"
    android:exported="false"
    android:screenOrientation="portrait" />
```

游戏页至少需要两个容器：

```xml
<FrameLayout
    android:id="@+id/op_game_container"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />

<androidx.constraintlayout.widget.ConstraintLayout
    android:id="@+id/op_widget_container"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

`op_game_container` 挂载 SDK 游戏 View，`op_widget_container` 承载 Toast、弹窗、图片预览和视频等宿主 UI。游戏容器不要设置为 `gone` 或 `invisible`。

## 6. 打开 OP 游戏列表

```java
OPListActivity.start(this);
```

完整链路：

```text
SUDOP.initSDK
  -> 恢复有效本地会话，或调用 /api/auth/login
  -> SUDOP.getGameList 分页拉取游戏
  -> 按 gameId 去重并显示列表
  -> 用户选择游戏
  -> OPActivity.start(context, gameId, gameName)
```

列表从第 0 页开始，每页 20 条。实现会处理空页、页码不前进、重复 `gameId` 和异常超长分页，页面退出时会取消网络请求并废弃过期回调。

## 7. 启动游戏

`OPActivity` 调用：

```java
gameViewModel.switchGame(this, gameId);
```

`OPViewModel` 与 `BaseGameViewModel` 依次完成：

```text
请求最新 userSignature
  -> SUDOP.auth(userSignature)
  -> SUDOP.startGame(gameId, language, listener)
  -> onGameViewCreated 挂载游戏 View
  -> onCreated 保存句柄、注册 OPWrappedClient、设置方向
  -> onProgress / onSuccess 更新加载状态
```

不要缓存并重复使用过期的 `userSignature`。如果接入方不需要 Demo 的匿名登录，应替换 `OPApiClient` 与 `OPSessionStore`，但保留“业务登录态 -> 服务端签名 -> SDK 鉴权”的责任边界。

## 8. 生命周期与退出

`OPActivity` 必须把页面生命周期转发给 `OPViewModel`：

```java
@Override protected void onStart() {
    super.onStart();
    gameViewModel.onStart();
}

@Override protected void onResume() {
    super.onResume();
    gameViewModel.onResume();
}

@Override protected void onPause() {
    gameViewModel.onPause();
    super.onPause();
}

@Override protected void onStop() {
    gameViewModel.onStop();
    super.onStop();
}
```

页面关闭前调用 `gameViewModel.destroy()`。该方法会取消鉴权请求、销毁 `SUDOPGameTask` 和游戏句柄，并移除游戏 View。

## 9. 迁移 OP 到业务 App

至少迁移并适配：

1. `game/op/` 全部业务代码。
2. `SUDGIWrapper/base/` 与 `SUDGIWrapper/custom/` 中被 OP 使用的封装。
3. OP 布局、drawable、string 资源。
4. Manifest 页面、权限、FileProvider 与横竖屏配置。
5. OP 应用参数和业务服务端地址。
6. 自己的登录态、App Token 存储与 `userSignature` 接口。
7. `OPWrappedClient` 中用户资料、支付、相册、视频等回调的真实业务实现。

# GI（Game Interaction）接入

## 1. 适用场景

GI 示例适合传统互动小游戏房间：业务方已经知道 `gameId`，为玩家分配 `roomId`，从业务服务端获取短期 `code`，再通过 `SUDGI.loadMG` 加载游戏。相同游戏中的玩家只有使用同一 `roomId` 才会进入同一房间。

GI 与 OP 不共用账号会话。当前 Demo 为每次应用进程随机生成一个 `userId`；正式接入必须替换为业务系统中稳定且唯一的用户 ID。

## 2. 关键代码与资源

```text
project/QuickStart/src/main/java/global/sud/gi/hello/app/game/gi/
├── GIQuickStartActivity.java           # GI 首页、房间号输入与游戏选择
├── GIGameActivity.java                 # 游戏页面、容器、安全区与生命周期
├── GIBaseGameViewModel.java            # getCode -> initSDK -> loadMG 主链路
├── GIGameViewModel.java                # 业务参数、网络请求与状态交互示例
├── GIQuickStartUtils.java               # Demo 用户 ID 与日志工具
├── data/GIGameRepository.java          # 本地演示游戏清单
└── widget/                             # 房间 UI 与选择弹窗
```

主要布局：

```text
project/QuickStart/src/main/res/layout/activity_gi_quick_start.xml
project/QuickStart/src/main/res/layout/activity_gi_game.xml
project/QuickStart/src/main/res/layout/item_gi_home_game.xml
```

## 3. 配置 GI 参数

在 `AppConfig.java` 中配置：

```java
public static final String GI_APP_ID = "YOUR_GI_APP_ID";
public static final String GI_APP_KEY = "YOUR_GI_APP_KEY";
public static final String GI_LOGIN_URL = "https://your-app-server.example.com/login";
public static final boolean GI_GAME_IS_TEST_ENV = true;
public static final String GI_DEFAULT_ROOM_ID = "10000";
```

- `GI_APP_ID`、`GI_APP_KEY`：SUD 为当前应用分配的 GI 参数。
- `GI_LOGIN_URL`：业务服务端签发短期 `code` 的接口。
- `GI_GAME_IS_TEST_ENV`：必须与后台配置环境一致。
- `GI_DEFAULT_ROOM_ID`：仅用于 Demo；生产环境应由业务房间系统生成。

应用包名需要在 SUD 后台登记。仅修改 Java 包路径不会改变 Android `applicationId`；当前 Demo 的 `applicationId` 定义在 `project/QuickStart/build.gradle`。

## 4. 对接 GI 登录接口

`GIGameViewModel.getCode(...)` 当前向 `GI_LOGIN_URL` 发送：

```json
{
  "user_id": "业务用户 ID",
  "app_id": "GI App ID"
}
```

并读取：

```json
{
  "ret_code": 0,
  "data": {
    "code": "服务端签发的短期 code"
  }
}
```

正式接入时请改成自己的网络层，并遵守：

- 每次 SDK 请求 `code` 时都向业务服务端获取最新值，不缓存短期 `code`；
- `appSecret` 及签名逻辑只保留在服务端；
- 页面退出或切换游戏时取消未完成请求，忽略旧游戏的异步回调；
- SDK 回调 `onExpireCode` 时重新获取 `code`，然后调用 `updateCode`。

## 5. 注册页面与准备容器

```xml
<activity
    android:name="global.sud.gi.hello.app.game.gi.GIQuickStartActivity"
    android:exported="false"
    android:screenOrientation="portrait" />

<activity
    android:name="global.sud.gi.hello.app.game.gi.GIGameActivity"
    android:exported="false"
    android:launchMode="singleTask"
    android:screenOrientation="portrait" />
```

游戏 View 容器：

```xml
<FrameLayout
    android:id="@+id/game_container"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

容器必须保持可见。业务 UI 可以覆盖在游戏 View 上方，但需要在 `GIGameViewInfoModel.GameViewRectModel` 中声明安全操作区，避免游戏按钮被宿主 UI 遮挡。

## 6. 打开 GI 页面并加载游戏

打开 Demo 选择页：

```java
GIQuickStartActivity.start(this);
```

业务已持有房间号和游戏 ID 时可以直接进入：

```java
GIGameActivity.start(this, roomId, gameId);
```

游戏加载入口：

```java
gameViewModel.switchGame(this, roomId, gameId);
```

完整链路：

```text
校验 roomId 和 gameId
  -> 销毁上一个游戏并取消旧请求
  -> 从业务服务端获取短期 code
  -> SUDGI.initSDK(appId, appKey, userId, environment)
  -> SUDGI.loadMG(userId, roomId, code, gameId, language)
  -> getGameView
  -> Activity 将 View 添加到 game_container
```

`gameId <= 0` 表示关闭当前游戏。切换游戏时，`GIBaseGameViewModel` 使用加载代次检查，避免前一个请求的迟到回调重新加载已销毁的游戏。

## 7. 游戏列表与房间

`GIGameRepository` 是静态演示数据，不会从平台动态同步。迁移时可以：

- 用自己的后台游戏列表替换 `GIGameRepository`；
- 保留 `GIGameModel`，由业务接口填充 `gameId`、名称和图标；
- 绕过选择页，通过 `GIGameActivity.start(...)` 打开指定游戏。

`roomId` 由接入方定义，应保证房间生命周期内稳定且可追踪。不要使用昵称等可能变化或泄露个人信息的值作为房间号。

## 8. 生命周期与销毁

GI 页面至少转发：

```java
@Override protected void onResume() {
    super.onResume();
    gameViewModel.onResume();
}

@Override protected void onPause() {
    super.onPause();
    gameViewModel.onPause();
}
```

`onResume()` 对应 `playMG()`，`onPause()` 对应 `pauseMG()`。返回、点击关闭、主动 `finish()` 和 `onDestroy()` 前都必须调用 `destroyMG()`，用于取消延迟重试和网络请求、废弃旧回调、销毁状态机对象并移除游戏 View。

## 9. 安全区、配置与状态交互

在调用 `switchGame(...)` 前完成配置：

```java
GIGameConfigModel config = gameViewModel.getGIGameConfigModel();
config.ui.ping.hide = false;

gameViewModel.gameViewRectModel = gameViewRectModel;
gameViewModel.languageCode = "zh-CN";
```

- `getGameRect(...)` 返回游戏 View 尺寸与安全操作区域。
- `getGIGameConfigModel()` 返回游戏 UI/能力配置，加载后再修改不会生效。
- `GIFSTAPPDecorator` 封装 App 主动发送给游戏的状态。
- `GIFSMMGListener` / `GIFSMMGDecorator` 接收游戏回调给 App 的状态。

发送涉及网络的状态后不要立即销毁页面，具体等待与回调规则以对应状态接口文档为准。

## 10. 迁移 GI 到业务 App

至少迁移并适配：

1. `game/gi/` 下需要的页面、ViewModel、模型和 widget。
2. `SUDGIWrapper/gi/` 中的状态机封装和模型。
3. GI 布局、图片、字符串与主题资源。
4. Manifest 页面、权限、横竖屏与配置变化处理。
5. 自己的用户 ID、房间 ID、游戏列表与短期 `code` 接口。
6. 游戏安全区、语言、游戏 UI 配置和业务状态交互。
7. release 混淆规则，以及真机进房、切后台、切游戏、退出和断网重试验证。

# OP 与 GI 共存注意事项

- 两套配置使用不同前缀：`OP_*` 与 `GI_*`，不要交叉引用。
- OP 使用 `userSignature`；GI 使用短期 `code`，二者不可替换。
- OP 游戏列表来自 SUDOP；GI Demo 列表来自本地静态数据。
- 两套页面各自持有并销毁游戏实例，不要用一个全局单例同时管理两者。
- 首页语言设置会改变 App/Wrapper 资源语言；小游戏文案是否切换取决于传给 SDK 的语言和游戏资源。
- 升级 SDK 时同时检查 SUDGI、SUDOP、广告适配器、混淆规则和在线迁移说明。

# 构建、测试与发布检查

运行单元测试和 Debug 构建：

```shell
cd project
./gradlew :QuickStart:testOfficialDebugUnitTest
./gradlew :QuickStart:assembleOfficialDebug
```

检查 Release 构建：

```shell
cd project
./gradlew :QuickStart:assembleOfficialRelease
```

发布前至少确认：

- 已替换 Demo 的 OP/GI 参数、登录地址、用户 ID 和房间 ID；
- `applicationId` 已在对应控制台登记；
- 正式签名通过安全的本地或 CI 配置注入，未提交密码；
- `global.sud.gi.SUDGIWrapper.gi.**` 的 JSON 模型字段未被 R8 混淆；
- 根据目标市场选择广告适配器并完成隐私同意流程；
- 权限按实际功能裁剪，并完成相机、麦克风、相册等运行时授权；
- 真机验证 OP/GI 的首次进入、切后台恢复、切换游戏、主动退出、断网和凭证过期；
- 日志中没有 App Token、`userSignature`、短期 `code` 或服务端密钥。

# 在线文档

## OP（OpenPaaS）文档

- [OpenPaaS 接入前准备](https://developer.sud.tech/openpaas/app/guide/guide-dev/start.html)：客户端、业务服务端、控制台配置及密钥责任边界。
- [Android 端快速接入](https://developer.sud.tech/openpaas/app/guide/guide-dev/quick-integration/client/android-quick-start.html)：SDK 依赖、初始化、`auth`、`startGame`、广告和生命周期。
- [OpenPaaS 控制台](https://developer.sud.tech/openpaas/app/guide/guide-dev/op-console.html)：申请应用参数、登记包名及管理配置。
- [服务端生成 userSignature](https://developer.sud.tech/openpaas/app/guide/guide-dev/quick-integration/server/data-security/user-signature-guide.html)：签名载荷、有效期和服务端生成要求。
- [`SUDOP.startGame` Android API](https://developer.sud.tech/openpaas/app/client-api/android-api-reference/sudop/startGame.html)：按游戏 ID 启动、回调、View 挂载与任务销毁。

## GI（Game Interaction）文档

- [GI Android 快速开始](https://github.com/SudTechnology/sud-mgp-doc/blob/master/zh-CN/app/Client/StartUp-Android.md)：`getCode -> initSDK -> loadMG` 接入时序。
- [GI 文档 GitHub 仓库](https://github.com/SudTechnology/sud-mgp-doc)：在线文档源文件与客户端文档索引。
- [游戏语言参数](https://docs.sud.tech/zh-CN/app/Client/Languages/)：`loadMG` 的语言代码说明。
- [游戏 View 与安全区](https://github.com/SudTechnology/sud-mgp-doc/blob/master/zh-CN/app/Client/API/ISudFSMMG/onGetGameViewInfo.md)：`view_size` 和 `view_game_rect` 回调格式。
- [App 向游戏发送状态（APPFST）](https://docs.sud.tech/zh-CN/app/Client/APPFST/)：加入、准备、队伍、麦克风等 App 状态。
- [游戏向 App 回调状态（MGFSM）](https://docs.sud.tech/zh-CN/app/Client/MGFSM/)：游戏状态和玩家状态回调。

## 示例与源码参考

- [上游中文 README](https://github.com/SudTechnology/Hello-SUD-OpenPaaS-Android/blob/master/zh_README.md)
- [本项目仓库](https://github.com/SudTechnology/Hello-SUD-OpenPaaS-Android)

在线文档会随 SDK 更新。升级 SDK 前应以对应版本的发布说明和 API 文档为准，并重新验证接口、广告依赖与混淆配置。

# 常见问题

## OP 列表能打开，但进入游戏鉴权失败

依次检查 `APP_BASE_URL`、App Token 有效期、签名中的 App ID 与用户 ID、`userSignature` 有效期，以及控制台登记的 `applicationId` 是否与安装包一致。

## GI 一直重复请求登录接口

`GIBaseGameViewModel` 在获取 `code`、SDK 初始化或加载失败后会延迟重试。检查返回格式、`ret_code`、`code`、测试/生产环境以及 App ID/App Key。退出页面会取消重试；如果退出后仍有请求，说明业务网络层没有正确实现取消。

## 游戏 View 不显示或布局异常

- 确认 SDK 回调的 View 已在主线程添加到可见 `FrameLayout`。
- 不要把游戏容器设置为 `gone` 或 `invisible`。
- GI 检查安全区；OP 检查游戏容器与宿主 UI 容器层级。
- Release 异常而 Debug 正常时，优先检查 R8/资源混淆规则。

## 切后台或退出后仍有声音/请求

- OP：确认转发 `onStart/onResume/onPause/onStop`，并在退出时调用 `destroy()`。
- GI：确认转发 `onResume/onPause`，并在所有退出路径调用 `destroyMG()`。
- 确认自定义网络层会取消请求，且异步回调在页面销毁后不会再次挂载游戏。
