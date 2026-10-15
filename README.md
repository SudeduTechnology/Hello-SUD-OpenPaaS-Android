[中文文档](./zh_README.md)

# Hello-SUD-OpenPaaS-Android

This project contains two independent Android game integration examples:

- **OP (OpenPaaS)** uses `SUDOP` to fetch the platform catalog, authenticate the current user, and launch a game.
- **GI (Game Interaction)** uses `SUDGI` to load an interactive game by `gameId + roomId` and exchange states between the host app and the game.

The paths share selected host capabilities from `SUDGIWrapper`, but use different entry pages, configuration values, credentials, and lifecycle APIs. An OP `userSignature` cannot be used as a GI short-lived `code`.

## OP and GI at a glance

| Item | OP (OpenPaaS) | GI (Game Interaction) |
| --- | --- | --- |
| SDK entry | `global.sud.op.runtime.core.SUDOP` | `global.sud.gi.core.SUDGI` |
| Demo entry | `OPListActivity.start(context)` | `GIQuickStartActivity.start(context)` |
| Catalog | Paginated `SUDOP.getGameList(...)` | Local demo data in `GIGameRepository` |
| Credential | Backend-issued `userSignature` | Backend-issued short-lived `code` |
| Launch input | `gameId`, language | `gameId`, `roomId`, language, `code` |
| Launch API | `SUDOP.startGame(...)` | `SUDGI.loadMG(...)` |
| Lifecycle | `start / play / pause / stop / destroy` | `playMG / pauseMG / destroyMG` |
| Host bridge | `SUDOPWrappedClient` | `GIFSTAPPDecorator` and `GIFSMMGDecorator` |

## Requirements

- Android Studio Narwhal Feature Drop (2025.1.2, paired with the current AGP 8.12.0) or another IDE version compatible with AGP 8.12.0
- JDK 17
- Android Gradle Plugin 8.12.0
- Gradle 8.13 through the included wrapper
- compileSdk 36, targetSdk 36, and minSdk 26
- Java 8 source compatibility

The shared SDK version is declared in `project/SUDGIWrapper/build.gradle`; the current value is `2.1.0`.

## Run the demo

1. Open the `project/` directory in Android Studio.
2. Let Gradle sync and verify Android SDK 36 and JDK 17.
3. Select the `QuickStart` module and `officialDebug` variant.
4. Run on Android 8.0 (API 26) or newer.
5. Choose **OpenPaaS Game** or **GI Game** on the home page.

Command-line build:

```shell
cd project
./gradlew :QuickStart:assembleOfficialDebug
```

The APK is generated under `project/QuickStart/build/outputs/apk/official/debug/`.

## Project layout

```text
project/
├── QuickStart/
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/global/sud/gi/hello/app/
│       │   ├── base/AppConfig.java     # OP and GI demo configuration
│       │   ├── game/op/                # Complete OP flow
│       │   ├── game/gi/                # Complete GI flow
│       │   └── ui/main/                # Home and language settings
│       └── res/                        # Layout, image, and locale resources
└── SUDGIWrapper/
    └── src/main/java/global/sud/gi/SUDGIWrapper/
        ├── base/                       # OP lifecycle wrapper
        ├── custom/                     # Host image, video, and dialog features
        └── gi/                         # GI state, model, and decorator wrappers
```

The launcher flow reaches `global.sud.gi.hello.app.ui.main.MainActivity`. The home page only routes to OP or GI; their business implementations do not call each other.

## Shared SUDGIWrapper module

Import `project/SUDGIWrapper`, or declare it as a Gradle module and add:

```groovy
dependencies {
    implementation project(':SUDGIWrapper')
}
```

The module includes SDK/ad-adapter dependencies, the OP `BaseGameViewModel`, and GI state wrappers.

Before publishing to Google Play, review the SDK flavor, ad sources, and privacy configuration in `project/SUDGIWrapper/build.gradle`. Do not copy every demo ad dependency unless required.

# OP (OpenPaaS) integration

## When to use OP

The OP example covers the following flow:

- fetch the game list for the current application or selection from OpenPaaS;
- sign in through the application's own account system;
- obtain a `userSignature` from the application backend;
- call `SUDOP.initSDK` to initialize the SDK;
- call `SUDOP.auth` to synchronize the user's login state;
- call `SUDOP.startGame` to launch the selected game.

## Key files

```text
project/QuickStart/src/main/java/global/sud/gi/hello/app/game/op/
├── OPListActivity.java                 # SDK init, account setup, catalog, retry UI
├── OPActivity.java                     # Game screen, View mounting, lifecycle
├── OPViewModel.java                    # userSignature and launch parameters
├── OPWrappedClient.java                # Game-to-app host capabilities
├── data/                               # Local session model and storage
├── net/                                # Login and userSignature requests
├── repository/                         # Pagination, deduplication, cancellation
└── ui/                                 # Catalog UI and localized-field fallback
```

Main layouts are `activity_op_list.xml`, `activity_op.xml`, and `item_op.xml`.

## Configure OP

Update `project/QuickStart/src/main/java/global/sud/gi/hello/app/base/AppConfig.java`:

```java
public static final String OP_APP_ID = "YOUR_OP_APP_ID";
public static final String OP_APP_KEY = "YOUR_OP_APP_KEY";
public static final String OP_SELECTION_ID = "YOUR_SELECTION_ID";
public static String APP_BASE_URL = "https://your-app-server.example.com";
```

- `OP_APP_ID` and `OP_APP_KEY` initialize SUDOP and must match the registered Android app.
- `OP_SELECTION_ID` scopes the catalog. An empty value uses the overload without a selection ID.
- `APP_BASE_URL` is the application's own backend address.
- Replace demo values and register the production `applicationId` before release.

## Application backend

`OPApiClient` demonstrates:

```text
POST /api/auth/login
POST /api/openpaas/user-signature
```

`/api/auth/login` is the application's own guest login endpoint. It sends an empty JSON body and an `Idempotency-Key` to avoid creating duplicate guest users. The returned App Token, expiry, and profile are stored locally; an expired local session triggers a new login.

Request:

```http
POST /api/auth/login HTTP/1.1
Content-Type: application/json
Idempotency-Key: <stable-random-key-of-8-to-200-characters>

{}
```

Successful response (`200 OK`; `appTokenExpiresIn` is measured in seconds):

```json
{
  "appToken": "<opaque-app-token>",
  "appTokenExpiresIn": 2592000,
  "uid": 10000,
  "nickname": "Little Star",
  "avatarUrl": "https://cdn.example.com/avatars/10000/avatar.png",
  "casdoorBound": false
}
```

`/api/openpaas/user-signature` obtains the `userSignature` required for OP SDK authentication. It sends `Authorization: Bearer <App Token>` and verifies that the returned App ID and user ID match the current session. HTTP 401 clears the session, logs in again, and retries once.

Request:

```http
POST /api/openpaas/user-signature HTTP/1.1
Authorization: Bearer <opaque-app-token>
Content-Type: application/json

{}
```

Successful response (`200 OK`; `expiresAt` is an ISO 8601 timestamp):

```json
{
  "appId": "YOUR_OP_APP_ID",
  "user": {
    "id": 10000,
    "name": "Little Star",
    "avatar": "https://cdn.example.com/avatars/10000/avatar.png"
  },
  "userSignature": "<short-lived-user-signature>",
  "expiresAt": "2026-10-15T12:00:00.000Z"
}
```

Both endpoints use the same error envelope for non-2xx responses:

```json
{
  "error": {
    "code": "APP_TOKEN_INVALID_OR_EXPIRED",
    "message": "App Token is invalid or expired",
    "requestId": "<request-id>"
  }
}
```

Generate `userSignature` on the backend. Keep the data-encryption key, application private key, App Secret, and signing logic out of the APK. Never put tokens, signatures, short-lived codes, or server keys in intents, URLs, logs, or analytics.

## Activity and container setup

Register `OPListActivity` and `OPActivity` as non-exported activities. Preserve the orientation and `configChanges` values from the demo manifest.

The game page needs a visible `FrameLayout` for the SDK game View and a second host-widget container for toast, modal, image, and video UI. Do not set the game container to `gone` or `invisible`.

## Catalog and launch flow

Open the catalog with:

```java
OPListActivity.start(this);
```

```text
SUDOP.initSDK
  -> restore a valid session or POST /api/auth/login
  -> fetch every SUDOP.getGameList page
  -> deduplicate by gameId and render the catalog
  -> OPActivity.start(context, gameId, gameName)
```

Pagination starts at page 0 with 20 records per page. The implementation handles empty pages, non-advancing pages, duplicate IDs, cancellation, and stale callbacks.

`OPActivity` starts a game through:

```java
gameViewModel.switchGame(this, gameId);
```

```text
request a fresh userSignature
  -> SUDOP.auth(userSignature)
  -> SUDOP.startGame(gameId, language, listener)
  -> mount the View from onGameViewCreated
  -> save the handle, register OPWrappedClient, and set orientation
  -> update loading through onProgress / onSuccess
```

Do not cache an expired `userSignature`. If anonymous demo login is not appropriate, replace `OPApiClient` and `OPSessionStore` while preserving business login -> backend signature -> SDK authentication.

## OP lifecycle and migration

Forward Activity `onStart`, `onResume`, `onPause`, and `onStop` to `OPViewModel`. Call `gameViewModel.destroy()` before close; it cancels authentication work, destroys the `SUDOPGameTask` and handle, and removes the View.

When migrating, include the `game/op/` package, required `SUDGIWrapper/base/` and `custom/` code, OP resources, manifest/permissions/FileProvider settings, backend integration, and `OPWrappedClient` business callbacks.

# GI (Game Interaction) integration

## Key files

```text
project/QuickStart/src/main/java/global/sud/gi/hello/app/game/gi/
├── GIQuickStartActivity.java           # Room input and game selection
├── GIGameActivity.java                 # Container, safe area, lifecycle
├── GIBaseGameViewModel.java            # getCode -> initSDK -> loadMG
├── GIGameViewModel.java                # Business values, HTTP, state examples
├── GIQuickStartUtils.java               # Demo user ID and timestamp helpers
├── data/GIGameRepository.java          # Local demo catalog
└── widget/                             # Room UI and game picker
```

Main layouts are `activity_gi_quick_start.xml`, `activity_gi_game.xml`, and `item_gi_home_game.xml`.

## Configure GI

```java
public static final String GI_APP_ID = "YOUR_GI_APP_ID";
public static final String GI_APP_KEY = "YOUR_GI_APP_KEY";
public static final String GI_LOGIN_URL = "https://your-app-server.example.com/login";
public static final boolean GI_GAME_IS_TEST_ENV = true;
public static final String GI_DEFAULT_ROOM_ID = "10000";
```

The App ID/Key and test/production flag must match the registered GI app. `GI_LOGIN_URL` returns a short-lived code. `GI_DEFAULT_ROOM_ID` is demo-only; production rooms belong to the application's room service.

The demo generates a random user ID per app process. Replace it with a stable unique ID from the production account system. Register the final Android `applicationId`; changing a Java package does not change it.

## GI login endpoint

`GIGameViewModel.getCode(...)` posts:

```json
{
  "user_id": "business user ID",
  "app_id": "GI App ID"
}
```

and reads:

```json
{
  "ret_code": 0,
  "data": {
    "code": "short-lived backend code"
  }
}
```

Fetch a fresh code whenever the SDK requests one. Keep App Secret/signing logic on the backend, cancel pending work on exit/switch, ignore stale callbacks, and refresh through `updateCode` when `onExpireCode` fires.

## Activity, room, and launch flow

Register `GIQuickStartActivity` and `GIGameActivity` as non-exported activities. Use a visible `FrameLayout` for the game View. If host controls overlay it, report a matching safe interaction area through `GIGameViewInfoModel.GameViewRectModel`.

Open the picker:

```java
GIQuickStartActivity.start(this);
```

Or launch a known room/game directly:

```java
GIGameActivity.start(this, roomId, gameId);
```

The page calls:

```java
gameViewModel.switchGame(this, roomId, gameId);
```

```text
validate roomId and gameId
  -> destroy the previous game and cancel stale work
  -> request a short-lived code
  -> SUDGI.initSDK(appId, appKey, userId, environment)
  -> SUDGI.loadMG(userId, roomId, code, gameId, language)
  -> obtain and mount the game View
```

A `gameId <= 0` closes the game. Load generations prevent older callbacks from restoring a destroyed game.

`GIGameRepository` is static demo data, not a platform catalog. Replace it with a backend catalog or bypass the picker. The integrator owns `roomId`; keep it stable for the room lifetime and do not derive it from a nickname or another mutable/personal value.

## GI lifecycle, safe area, and states

Forward Activity `onResume` and `onPause` to the ViewModel; they map to `playMG()` and `pauseMG()`. Call `destroyMG()` on every back, close, `finish()`, and destruction path.

Configure safe area, language, and game UI before `switchGame(...)`:

```java
GIGameConfigModel config = gameViewModel.getGIGameConfigModel();
config.ui.ping.hide = false;
gameViewModel.gameViewRectModel = gameViewRectModel;
gameViewModel.languageCode = "en-US";
```

`GIFSTAPPDecorator` sends App states to the game. `GIFSMMGListener` / `GIFSMMGDecorator` receive game/player states. Do not destroy the page immediately after sending a network-backed state; follow the timing and callbacks documented for that state.

When migrating, include required `game/gi/` code, `SUDGIWrapper/gi/`, resources, manifest and permissions, production identity/room/catalog/code services, safe-area and state logic, release keep rules, and real-device lifecycle/failure validation.

# Running OP and GI together

- Keep `OP_*` and `GI_*` configuration separate.
- OP `userSignature` and GI short-lived `code` are not interchangeable.
- OP fetches its catalog through SUDOP; the GI demo uses local data.
- Each screen owns and destroys its game instance; do not manage both with one global singleton.
- App/Wrapper language settings do not automatically translate text embedded in a game.
- Review SUDGI, SUDOP, ad adapters, keep rules, and migration notes together when upgrading.

# Build, test, and release checks

```shell
cd project
./gradlew :QuickStart:testOfficialDebugUnitTest
./gradlew :QuickStart:assembleOfficialDebug
./gradlew :QuickStart:assembleOfficialRelease
```

Before release, replace demo values, register the final package, keep signing secrets outside version control, preserve GI JSON model fields from R8, configure consent and only required ad adapters, minimize permissions, test both flows on devices, and confirm logs contain no tokens, signatures, short-lived codes, or server keys.

# Online documentation

## OP (OpenPaaS)

- [Integration prerequisites](https://developer.sud.tech/en/openpaas/app/guide/guide-dev/start.html) — client, backend, console, and key ownership.
- [Android quick integration](https://developer.sud.tech/en/openpaas/app/guide/guide-dev/quick-integration/client/android-quick-start.html) — dependencies, initialization, `auth`, `startGame`, ads, and lifecycle.
- [OpenPaaS console](https://developer.sud.tech/openpaas/app/guide/guide-dev/op-console.html) — application values and Android package registration.
- [Generate userSignature](https://developer.sud.tech/openpaas/app/guide/guide-dev/quick-integration/server/data-security/user-signature-guide.html) — backend payload, expiry, and generation requirements.
- [`SUDOP.startGame` Android API](https://developer.sud.tech/openpaas/app/client-api/android-api-reference/sudop/startGame.html) — launch callbacks, View mounting, and task cleanup.

## GI (Game Interaction)

- [GI Android quick start](https://github.com/SudTechnology/sud-mgp-doc/blob/master/en-US/app/Client/StartUp-Android.md) — `getCode -> initSDK -> loadMG` sequence.
- [GI documentation source](https://github.com/SudTechnology/sud-mgp-doc) — source files and client documentation index.
- [Game language parameter](https://docs.sud.tech/en-US/app/Client/Languages/) — language passed to `loadMG`.
- [Game View and safe area](https://github.com/SudTechnology/sud-mgp-doc/blob/master/en-US/app/Client/API/ISudFSMMG/onGetGameViewInfo.md) — `view_size` and `view_game_rect`.
- [App-to-game states (APPFST)](https://docs.sud.tech/en-US/app/Client/APPFST/) — join, ready, team, microphone, and other host states.
- [Game-to-app states (MGFSM)](https://docs.sud.tech/en-US/app/Client/MGFSM/) — game and player callbacks.

## Examples and source

- [Upstream Chinese README](https://github.com/SudTechnology/Hello-SUD-OpenPaaS-Android/blob/master/zh_README.md)
- [Project repository](https://github.com/SudTechnology/Hello-SUD-OpenPaaS-Android)

Online documentation changes with the SDK. Use the documentation for the selected SDK version and revalidate APIs, ad dependencies, and keep rules when upgrading.

# Troubleshooting

## OP catalog works, but game authentication fails

Check `APP_BASE_URL`, App Token validity, signature App ID/user ID, `userSignature` expiry, and whether the installed `applicationId` matches the console entry.

## GI repeatedly calls the login endpoint

`GIBaseGameViewModel` retries after code, SDK initialization, or game load failures. Check the response shape, `ret_code`, `code`, environment, and App ID/Key. Requests after exit usually mean the production HTTP layer does not cancel correctly.

## The game View is blank or misplaced

Mount the callback View on the main thread into a visible `FrameLayout`. Check the GI safe area or OP game/widget z-order. If only release fails, inspect R8/resource-obfuscation rules.

## Audio or requests continue after exit

- OP: forward `onStart/onResume/onPause/onStop` and call `destroy()`.
- GI: forward `onResume/onPause` and call `destroyMG()` from every exit path.
- Cancel custom HTTP calls and reject callbacks from destroyed screens.
