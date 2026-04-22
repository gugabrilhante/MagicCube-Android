For the best user experience, you should optimize your app to make it as small
and fast as possible. Our app optimizer, called R8, streamlines your app by
removing unused code and resources, rewriting code to optimize runtime
performance, and more. To your users, this means:

- Faster startup time
- Reduced memory usage
- Improved rendering and runtime performance
- Fewer ANRs

> **Important:** You should always enable optimization for your app's release build; however, you probably don't want to enable it for tests or libraries.

## R8 optimization overview

R8 uses a multi-phase process to optimize your app for size and speed. Key
operations include the following:

- **Code shrinking (also known as tree shaking)**: R8 identifies and removes unreachable code from your application and its library dependencies.

- **Logical optimizations**: R8 rewrites your code to improve execution efficiency and reduce overhead. Key techniques include:
  - **Method inlining**: R8 replaces a method call site with the actual body of the called method.
  - **Class merging**: R8 combines sets of classes and interfaces into a single class.

- **Obfuscation (also known as minification)**: To reduce the size of the DEX file, R8 shortens the names of classes, fields, and methods.

## Enable optimization

To enable app optimization, set `isMinifyEnabled = true` (for code optimization)
and `isShrinkResources = true` (for resource optimization) in your release build's
app-level build script.

### Kotlin

```kotlin
android {
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                ...
            )
        }
    }
}
```

### Groovy

```groovy
android {
    buildTypes {
        release {
            minifyEnabled = true
            shrinkResources = true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt')
        }
    }
}
```

## AGP and R8 version behavior changes

| AGP version | Features introduced |
|---|---|
| 9.1 | **Classes repackaged by default** |
| 9.0 | **Optimized resource shrinking enabled by default**, Library rule filtering, Kotlin null checks optimized by default |
| 8.12 | **Resource shrinking initial support**, Logcat retracing |
| 8.6 | **Improved retracing** |
| 8.0 | **Full mode by default** |
| 7.0 | **Full mode available as opt-in** |

## Enable optimized resource shrinking (AGP < 9.0)

For AGP versions between 8.6 and 9.0, explicitly enable the new resource shrinker:

    android.r8.optimizedResourceShrinking=true

If you are using AGP 9.0.0 or newer, optimized resource shrinking is automatically
applied when `isShrinkResources = true`.

## Verify R8 full mode

Remove the following line from `gradle.properties` if it exists:

    android.enableR8.fullMode=false # Remove this line
