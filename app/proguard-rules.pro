# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /sdk/tools/proguard/proguard-android.txt

# Retain Hilt generated code
-keep class * extends com.google.dagger.hilt.internal.GeneratedCompilerModule
-keepclassmembers class ** { @dagger.hilt.android.HiltAndroidApplication *; }
-keepclassmembers class ** { @dagger.hilt.android.lifecycle.HiltViewModel *; }
