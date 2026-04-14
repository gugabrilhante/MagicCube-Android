# Magic Cube Android

An interactive 3D Rubik's Cube simulator for Android, built with Clean Architecture + MVVM, Jetpack Compose, Koin, Navigation3, and OpenGL ES 3.0.

## Demo

| <img src="docs/magic_cube_options.gif" width="45%"> | <img src="docs/magic_cube_gameplay.gif" width="45%"> |

> Rotate the cube freely with drag gestures. Swipe to rotate individual face slices. Shuffle and solve at your own pace.

---

## Features

- **3D Rendering** via OpenGL ES 3.0 with MVP matrix transformations
- **Touch interaction**: drag to rotate the cube freely, swipe to rotate a face slice
- **Inertia physics**: smooth momentum-based rotation that decays naturally
- **Closest-face detection**: swipe automatically targets the face nearest to the screen
- **Shuffle**: configurable number of random rotations (10–100 moves)
- **Settings**: tune shuffle count, rotation speed, and cube size
- **Animations**: animated gradient backgrounds on menu screens
- **Multilingual**: English, Portuguese (Brazil), and Spanish

---

## Architecture

The project follows **Clean Architecture + MVVM**, organized into three strict layers. The Domain layer has **zero Android or third-party dependencies**.

```
app/
├── domain/          # Pure Kotlin: models, repository interfaces, use cases
│   ├── CubeSettings.kt
│   ├── TimeProvider.kt
│   ├── repository/
│   │   └── SettingsRepository.kt
│   └── usecase/
│       ├── ObserveSettingsUseCase.kt
│       └── SaveSettingsUseCase.kt
│
├── data/            # DataStore implementation of domain contracts
│   ├── DataStoreSettingsDataSource.kt
│   ├── SettingsLocalDataSource.kt
│   └── SettingsRepositoryImpl.kt
│
├── presentation/    # ViewModels + UI state models
│   ├── MainMenuViewModel.kt
│   ├── cube/
│   │   ├── CubeViewModel.kt
│   │   └── CubeRenderState.kt
│   └── options/
│       ├── OptionsViewModel.kt
│       └── OptionsUiState.kt
│
├── compose/         # Jetpack Compose screens
│   ├── MainMenuScreen.kt
│   ├── OptionsScreen.kt
│   ├── CubeScreen.kt          ← OpenGL via AndroidView (replaces MagicCubeActivity)
│   └── components/            # MagicCubeButton, CollapsibleCard, AnimatedBackground…
│
├── navigation/      # Navigation3 — route definitions and NavDisplay host
│   ├── AppRoutes.kt
│   └── AppNavigation.kt
│
├── grafic/          # OpenGL engine and 3D cube logic (no domain dependency)
│   ├── ICubeGameEngine.kt     ← interface; injected into CubeViewModel
│   ├── CubeGameEngineFactory.kt
│   ├── CubeGameEngine.kt
│   ├── CubeSurfaceView.kt     ← custom GLSurfaceView with touch dispatch
│   ├── CubeRenderer.kt
│   ├── Cube.kt
│   ├── CubeShader.kt
│   └── MatrixTracker.kt
│
├── di/              # Koin module
│   └── AppModule.kt
│
└── activity/
    └── MainMenuActivity.kt    ← single-Activity entry point
```

### Layer rules

| Layer | May depend on | Must NOT depend on |
|---|---|---|
| **Domain** | nothing | Android SDK, Retrofit, Room, Koin |
| **Data** | Domain | Presentation, Graphics |
| **Presentation** | Domain, Graphics (ICubeGameEngine) | Data (only through interfaces), Android Views |
| **Navigation** | Presentation, Compose | Data, Graphics |
| **Graphics** | Domain | Repository, DataStore |

---

## Navigation3

The app uses a **single Activity** (`MainMenuActivity`) with [Navigation3](https://developer.android.com/jetpack/compose/navigation3) managing all screen transitions.

```kotlin
// navigation/AppRoutes.kt
sealed class AppRoute {
    data object MainMenu : AppRoute()
    data object Cube     : AppRoute()
    data object Options  : AppRoute()
}

// navigation/AppNavigation.kt
@Composable
fun AppNavigation() {
    val backStack = rememberNavBackStack(AppRoute.MainMenu)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
    ) { key ->
        when (key) {
            AppRoute.MainMenu -> MainMenuScreen(
                onStartClick   = { backStack.add(AppRoute.Cube) },
                onOptionsClick = { backStack.add(AppRoute.Options) },
                onQuitClick    = { /* finish */ },
            )
            AppRoute.Cube    -> CubeScreen(onBack = { backStack.removeLastOrNull() })
            AppRoute.Options -> OptionsScreen()
        }
    }
}
```

Back-stack manipulation replaces Intent-based navigation. No `NavController` needed — the backstack is a plain `SnapshotStateList`.

---

## Dependency Injection (Koin)

All dependencies are declared in a single `AppModule`:

```kotlin
val appModule = module {
    // Data
    single<SettingsLocalDataSource> { DataStoreSettingsDataSource(androidContext()) }
    single<SettingsRepository>      { SettingsRepositoryImpl(get(), get()) }

    // Domain use cases
    singleOf(::SaveSettingsUseCase)
    singleOf(::ObserveSettingsUseCase)

    // Engine factory — returns ICubeGameEngine; swappable in tests
    single<CubeGameEngineFactory> { CubeGameEngineFactory { n -> CubeGameEngine(n) } }

    // System utilities
    single<TimeProvider> { TimeProvider { System.currentTimeMillis() } }

    // ViewModels
    viewModelOf(::MainMenuViewModel)
    viewModelOf(::CubeViewModel)
    viewModelOf(::OptionsViewModel)
}
```

### Testability

`CubeViewModel` receives:
- `CubeGameEngineFactory` → production uses `CubeGameEngine`; tests supply `FakeCubeGameEngine`
- `TimeProvider` → production uses `System.currentTimeMillis()`; tests control wall-clock time exactly

```kotlin
// unit test
val viewModel = CubeViewModel(
    observeSettings = ObserveSettingsUseCase(FakeSettingsRepository()),
    engineFactory   = CubeGameEngineFactory { _ -> FakeCubeGameEngine() },
    timeProvider    = TimeProvider { fakeTime },
)
```

---

## 3D Rendering

The rendering engine is built directly on **OpenGL ES 3.0** with no third-party 3D framework.

### Pipeline

1. `CubeScreen` (Compose) hosts a `CubeSurfaceView` via `AndroidView`
2. `CubeSurfaceView` owns touch-event dispatch → forwards to `CubeViewModel`
3. `CubeRenderer` (`GLSurfaceView.Renderer`) calls `viewModel.buildFrame()` each frame
4. `buildFrame()` computes one `CubeDrawCommand` per piece (27 total) with its MVP matrix
5. Each `Cube.draw(mvpMatrix)` uploads geometry and triggers a GLES draw call

### Shaders (GLSL ES 3.0)

| Shader | Role |
|---|---|
| Vertex | Transforms vertices through MVP; passes per-vertex color |
| Fragment | Flat shading — solid color per face, no lighting model |

### Matrix Transformations

`MatrixTracker` manages the 4×4 model-view stack. Slice rotations use a center-translate → rotate → inverse-translate sequence so each piece orbits its slice center.

---

## Touch Interaction

Touch events are captured in `CubeSurfaceView.onTouchEvent` and forwarded to `CubeViewModel`:

| Gesture | Threshold | Action |
|---|---|---|
| **Drag** | elapsed > 250 ms | Freely rotates the entire cube |
| **Swipe** | fast movement > 100 px in < 250 ms | Rotates the closest face slice |

`TimeProvider` is injected so the 250 ms threshold is fully deterministic in unit tests.

---

## Tests

### Unit tests (`src/test/`)

| Test class | What it covers |
|---|---|
| `CubeViewModelTest` | Movement classification, swipe→rotation, drag, inertia, engine delegation |
| `MainMenuViewModelTest` | Navigation event emission |
| `OptionsViewModelTest` | Increment/decrement/clamp/reset for all settings |
| `SaveSettingsUseCaseTest` | Persists settings through repository |
| `ObserveSettingsUseCaseTest` | Emits repository flow |
| `SettingsRepositoryTest` | Data layer save/load with fake DataSource |
| `SettingsFlowIntegrationTest` | Full vertical slice: Repository → UseCase → ViewModel |

### UI / Instrumented tests (`src/androidTest/`)

| Test class | What it covers |
|---|---|
| `MainMenuScreenTest` | Buttons visible; Quit callback fires |
| `OptionsScreenTest` | Setting labels and Reset button visible |

### Run all unit tests

```bash
./gradlew test
```

### Run instrumented tests (requires device or emulator)

```bash
./gradlew connectedAndroidTest
```

---

## Tech Stack

| Technology | Version | Usage |
|---|---|---|
| **Kotlin** | 2.0.21 | 100% Kotlin codebase |
| **Jetpack Compose** | BOM 2025.10 | All screens — Material Design 3 |
| **OpenGL ES 3.0** | — | 3D cube rendering |
| **Navigation3** | 1.0.0-alpha04 | Single-Activity, backstack-based navigation |
| **Koin** | 4.1.1 | DI for ViewModels, repositories, and engine factory |
| **Kotlin Coroutines & Flow** | 1.10.2 | `StateFlow` / `SharedFlow` state management |
| **DataStore Preferences** | 1.1.4 | Persistent settings storage |
| **JUnit 4** | 4.13.2 | Unit tests |
| **Compose UI Test** | BOM-managed | Instrumented UI tests |

---

## Project Setup

**Requirements:**
- Android Studio Meerkat (2024.3) or later
- Min SDK 21 / Target SDK 35
- Device or emulator with OpenGL ES 3.0 support

**Clone and run:**

```bash
git clone https://github.com/gugabrilhante/MagicCube-Android.git
```

Open in Android Studio and run on a physical device or emulator.

---

## License

This project is open source. See [LICENSE](LICENSE) for details.
