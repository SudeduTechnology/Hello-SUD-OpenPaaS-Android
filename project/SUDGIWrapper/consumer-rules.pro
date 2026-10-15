-keep class okhttp3.** {*;}
-keep class androidx.**{ *; }
-keep class okio.**{*;}

# GI QuickStart wrapper models are serialized to JSON and must keep field names.
-keep class global.sud.gi.SUDGIWrapper.gi.** { *; }
