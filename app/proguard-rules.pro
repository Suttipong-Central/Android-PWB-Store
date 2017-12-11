# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/napabhat/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

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

# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/CThawanapong/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class nameTh to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#Default
#keep all public and protected methods that could be used by java reflection
-keepclassmembernames class * { public protected <methods>; }
-keepclasseswithmembernames class * { native <methods>; }
-keepclasseswithmembernames class * { public <init>(android.content.Context, android.util.AttributeSet); }
-keepclasseswithmembernames class * { public <init>(android.content.Context, android.util.AttributeSet, int); }
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable { public static final android.os.Parcelable$Creator *; }
-keepattributes EnclosingMethod

# Butter Knife
# Retain generated class which implement Unbinder.
-keep public class * implements butterknife.Unbinder { public <init>(...); }

# Prevent obfuscation of types which use ButterKnife annotations since the simple nameTh
# is used to reflectively look up the generated ViewBinding.
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }

#Gson Config
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-keep class cenergy.central.com.pwb_store.model.** { *; }
-keep class cenergy.central.com.pwb_store.view.** { *; }
-keep class com.google.gson.** {*;}
-keep class sun.misc.Unsafe {*;}

#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.integration.okhttp3.OkHttpGlideModule


# Google Stuff
-keep class com.google.** { *; }
-keep class android.support.design.widget.** { *; }
-keep interface android.support.design.widget.** { *; }
-dontwarn com.google.**
-dontwarn android.support.design.**

# Filter out warnings that refer to legacy Code.
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep class org.apache.http.** { *; }
-keep interface org.apache.http.** { *; }
-keep class * implements org.apache.http{*;}
-keep class org.apache.avalon.framework.logger.** { *; }
-dontwarn android.net.http.AndroidHttpClient
-dontwarn org.apache.http.**
-dontwarn org.apache.commons.**

#EventBus
-keep class org.greenrobot.** {*;}
-keepattributes Subscribe
-keepclassmembers,includedescriptorclasses class ** { public void onEvent*(**); }
-keepclassmembers class ** {
    @org.greenrobot.event.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

#Retrofit 2
-dontwarn okio.**
-dontnote retrofit2.Platform
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
-dontwarn retrofit2.Platform$Java8
-dontwarn okhttp3.**
-keepattributes Signature
-keepattributes Exceptions

#Line Indicator
-dontwarn com.viewpagerindicator.LinePageIndicator

#Fast Scroller
-keep class com.simplecityapps.** {*;}

# autovalue
-dontwarn javax.lang.**
-dontwarn javax.tools.**
-dontwarn javax.annotation.**
-dontwarn autovalue.shaded.com.**
-dontwarn com.google.auto.value.**

-keep class com.microblink.** { *; }
-keepclassmembers class com.microblink.** { *; }
-dontwarn android.hardware.**
-dontwarn android.support.v4.**

# Simple XML
-dontwarn com.bea.xml.stream.**
-dontwarn org.simpleframework.xml.stream.**
-keep class org.simpleframework.xml.**{ *; }
-keepclassmembers,allowobfuscation class * {
    @org.simpleframework.xml.* <fields>;
    @org.simpleframework.xml.* <init>(...);
}

#Realm
-keep public class * extends io.realm.RealmObject { *; }
-keep class io.realm.annotations.RealmModule.** { *; }
-keep class io.realm.internal.Keep.** { *; }
-dontwarn javax.**
-dontwarn io.realm.**
