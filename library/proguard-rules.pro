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

#指定代码的压缩级别 0 - 7(指定代码进行迭代优化的次数，在Android里面默认是5，这条指令也只有在可以优化时起作用。)
-optimizationpasses 5

#混淆时不会产生形形色色的类名(混淆时不使用大小写混合类名)
-dontusemixedcaseclassnames

#指定不去忽略非公共的库类(不跳过library中的非public的类)
-dontskipnonpubliclibraryclasses

#指定不去忽略包可见的库类的成员
-dontskipnonpubliclibraryclassmembers

#不进行预校验,Android不需要,可加快混淆速度。
-dontpreverify

#混淆时记录日志(打印混淆的详细信息)
#这句话能够使我们的项目混淆后产生映射文件
#包含有类名->混淆后类名的映射关系
-verbose
-printmapping priguardMapping.txt

#指定混淆时采用的算法，后面的参数是一个过滤器
#这个过滤器是谷歌推荐的算法，一般不改变
-optimizations !code/simplification/artithmetic,!field/,!class/merging/

#错误显示行号
-keepattributes SourceFile,LineNumberTable

#屏蔽警告
-ignorewarnings

#保护代码中的Annotation不被混淆
#这在JSON实体映射时非常重要，比如fastJson
-keepattributes Annotation

################annotation###############
-keep class android.support.annotation.* { *; }
-keep class androidx.annotation.* { *; }
-keep interface android.support.annotation.* { *; }

#避免混淆泛型
#这在JSON实体映射时非常重要，比如fastJson
-keepattributes Signature

#保留所有的本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

#枚举类不能被混淆
-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}

#对R文件下的所有类及其方法，都不能被混淆
-keep class **.R$* {
    <fields>;
}
-keep class **.R$* {*;}
-keepclassmembers class **.R$* {
    public static <fields>;
}

#保留Parcelable序列化的类不能被混淆
-keep class * implements android.os.Parcelable {*;}
#保留Serializable 序列化的类不被混淆
-keepnames class * implements java.io.Serializable

#WebView相关
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
    public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
public void *(android.webkit.WebView, java.lang.String);
}
#Keep JavascriptInterface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface public *;
}

#保留了继承自Activity、Application这些类的子类
#因为这些子类有可能被外部调用
#比如第一行就保证了所有Activity的子类不要被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View

#这两个类我们基本也用不上，是接入Google原生的一些服务时使用的。
#-keep public class com.google.vending.licensing.ILicensingService
#-keep public class com.android.vending.licensing.ILicensingService

#如果有引用android-support-v4.jar包，可以添加下面这行
-keep public class com.null.test.ui.fragment.* {*;}

#保留Activity中的方法参数是view的方法，
#从而我们在layout里面编写onClick就不会影响
-keepclassmembers class * extends android.app.Activity {
public void * (android.view.View);
}

#自定义View
-keepnames class * extends android.view.View

################support###############
-keep class android.support.* { *; }
-keep interface android.support.* { *; }
-dontwarn android.support.*

#jar库不参与混淆
-libraryjars libs/xxx.jar

#--------(实体Model不能混淆，否则找不到对应的属性获取不到值)-----#
-keep class com.sonnyjack.library.bean.* {*;}

#----------------------------- 第三方 -----------------------------#

#Gson
# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}