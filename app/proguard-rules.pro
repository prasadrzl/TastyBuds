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

# Keep line numbers + source file for readable release crash reports (used by Crashlytics).
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

-keep class com.app.tastybuds.data.model.** { *; }
-keepclassmembers class com.app.tastybuds.data.model.** { *; }
-keepclassmembers class kotlin.Metadata { *; }

# Enums used via valueOf()/values() (e.g. OrderStatus, SenderType)
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Retrofit / OkHttp / Moshi / Room / Hilt / Firebase / Glide all ship their own consumer
# ProGuard rules, so no extra keeps are needed for them.