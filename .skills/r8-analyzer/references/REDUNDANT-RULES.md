This document outlines common "bad" or redundant keep rules for standard Android
development and popular libraries. Modern toolchains and libraries include their
own consumer keep rules embedded in their AAR/JAR files, making many manual
configurations unnecessary or even harmful to code optimization.

*** ** * ** ***

## Case: Global Keep Rules

**Common Mistakes:**
```proguard
-dontshrink
-dontobfuscate
-dontoptimize
```

**The Fix:** These keep rules completely disable the core optimizations of R8
for the entire codebase. They must be removed from the codebase.

*** ** * ** ***

## Case: Android Components

Keep rules required for Android components like Activity, Fragment, ViewModel,
Views, Services or Broadcast receivers are redundant. AAPT2 and R8 contain the
logic to automatically keep components declared in the `AndroidManifest.xml` or
referenced in XML layout files.

**Common Mistakes:**
```proguard
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.view.View
-keepclassmembers class * extends android.app.Fragment { public void *(android.view.View); }
```

**The Fix:** Delete these manual rules. AAPT2 handles this automatically.

*** ** * ** ***

## Case: Official Android and Kotlin Libraries

Keep rules targeting official library packages like AndroidX, Kotlin, and
Kotlinx are redundant as they are bundled within the libraries themselves.
Manual rules are often broader than what is strictly needed.

**Common Mistakes:**
```proguard
-keep class androidx.** { *; }
-keep class kotlinx.** { *; }
-keep class kotlin.** { *; }
```

**The Fix:** Delete these manual rules. Rely on the consumer keep rules packaged
within these dependencies.

*** ** * ** ***

## Case: Gson

### Overly Broad Data Model Rules

The most common mistake is keeping entire packages of data models (POJOs/DTOs),
keeping data models at all for deserialization is unnecessary.

    -keep class com.example.app.models.** { *; }
    -keep class com.example.app.package.models.* { *; }

### Redundant Interface & Adapter Rules

These rules added for TypeAdapter are unnecessary and are already covered by
the library, and prevent R8 from effectively shrinking and optimizing custom
adapters.

    -keep class * extends com.google.gson.TypeAdapter
    -keep class * implements com.google.gson.TypeAdapterFactory
    -keep class * implements com.google.gson.JsonSerializer
    -keep class * implements com.google.gson.JsonDeserializer

### Unnecessary TypeToken Rules

    -keep class com.google.gson.reflect.TypeToken { *; }
    -keep class * extends com.google.gson.reflect.TypeToken
    -keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken

### Internal and Example Packages

    -keep class com.google.gson.internal.** { *; }
    -keep class com.google.gson.internal.reflect.** { *; }
    -keep class com.google.gson.internal.UnsafeAllocator { *; }
    -keep class com.google.gson.stream.** { *; }

**The Fix:**

1. Use `@SerializedName` on every field in your data classes
2. Modern Gson (**v2.11.0+**) bundles its own rules. Delete keep rules that target classes used for gson serialization and deserialization.

*** ** * ** ***

## Case: Retrofit

Retrofit has shipped with its own consumer keep rules from 2.9.0 and higher.

### Blanket Library Preservation

    -keep class retrofit2.** { *; }
    -keep class retrofit2.api.** { *; }
    -keep class com.package.example.retrofit.api.** { *; }

### Manual Annotation Keeps

`-keepclasseswithmembers class * { @retrofit2.http.* <methods>; }`

### Redundant Network Response and Adapter Rules

    -keep,allowobfuscation,allowshrinking class retrofit2.Response
    -keep class retrofit2.adapter.rxjava2.Result { *; }

Fix: Verify you are using Retrofit 2.9.0 and higher.

*** ** * ** ***

## Case: Kotlin Coroutines

Kotlin Coroutines comes heavily optimized out of the box with embedded R8 rules.

### Blanket Coroutine Library Rules

`-keepclassmembers class kotlinx.coroutines.** { *; }`

### Redundant Internal Continuations

    -keepclassmembers class kotlin.coroutines.SafeContinuation { *; }
    -keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

### Dispatcher and Exception Handler Rules

    -keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
    -keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
    -keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
    -keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}

**Fix:** Remove any broad `kotlinx` keep rules. Coroutines (**v1.7.0+**) bundle the necessary keep rules.

*** ** * ** ***

## Case: Parcelable

**Common Mistakes:** Legacy projects often contain `-keep class * implements android.os.Parcelable { public static final android.os.Parcelable$Creator *; }`.

**The Fix:**

1. Add the `kotlin-parcelize` plugin.
2. Use `@Parcelize` annotation.
3. Delete all Parcelable rules.
4. The default `proguard-android-optimize.txt` contains the keep rules for keeping all the parcelable classes.
5. **Ideal Rule:** **None.** Delete all manual Parcelable keeps.

*** ** * ** ***

## Case: Room Database

**Common Mistakes:**

    -keep class * extends androidx.room.RoomDatabase
    -keep class *_*Impl { *; }

**The Fix:** Room generates its own ProGuard rules. Manual rules are redundant.

- **Ideal Rule:** **None.** Delete all manual Room or DAO keeps.

*** ** * ** ***

## Summary

If you have updated your libraries to the versions mentioned, your
`proguard-rules.pro` must not contain any keep rules for the libraries
mentioned here.
