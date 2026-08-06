# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# json序列化的混淆
-keep class global.sud.op.hello.service.req.** {*;}
-keep class global.sud.op.hello.service.resp.** {*;}
-keep class global.sud.op.hello.common.http.param.BaseBody {*;}
-keep class global.sud.op.hello.common.http.param.BaseResponse {*;}
-keep class global.sud.op.hello.ui.game.model.** {*;}

# Rx的混淆
-keep class retrofit2.adapter.rxjava3.** {*;}
-keep class retrofit2.converter.gson.** {*;}
-keep class io.reactivex.rxjava3.** {*;}

-keep class okhttp3.** {*;}
-keep class androidx.**{ *; }
-keep class okio.**{*;}