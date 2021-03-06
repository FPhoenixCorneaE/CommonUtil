#----------------------------------------------------------------------------
# Gson
-keep class com.google.gson.* {*;}
-keep class com.google.* {*;}
-keep class com.google.gson.stream.* {*;}
-keep class com.google.gson.examples.android.model.* {*;}
-keepattributes EnclosingMethod
-dontwarn com.google.**
#----------------------------------------------------------------------------

#----------------------------------------------------------------------------
# Bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.*{*;}
#----------------------------------------------------------------------------

#----------------------------------------------------------------------------
# Logger
-keep class com.orhanobut.logger.*{*;}
#----------------------------------------------------------------------------
