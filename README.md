# Magic Cube Android

An interactive 3D Rubik's Cube simulator for Android, built with Clean Architecture + MVVM with MVI-like unidirectional data flow, Jetpack Compose, Koin, Navigation3, and OpenGL ES 3.0.

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

The project follows **Clean Architecture + MVVM with MVI-like unidirectional data flow**, organized into layers that prioritize a single source of truth and clear separation of concerns.

### High-level Flow
UI (Compose) → ViewModel (intent dispatcher) → Interactor (application layer) → Domain (interaction logic) → Engine (cube state) → RenderEngine (frame generation) → Renderer (OpenGL)

### Components

- **CubeViewModel**: A thin orchestrator that acts as a state holder and intent dispatcher. It transforms UI intents into effects using the Interactor.
- **CubeGameInteractor (Application Layer)**: Orchestrates interactions between the ViewModel, domain logic, and the game engine. It holds the high-level logic for processing gestures and mapping them to engine commands.
- **CubeInteractionProcessor (Domain Layer)**: A pure Kotlin component containing the mathematical and gesture classification logic.
- **CubeRenderEngine**: Decouples the rendering abstraction from the domain logic, responsible for frame generation.
- **ICubeGameEngine**: Defines the contract for the cube's internal state and core mechanics.

```text
app/
├── domain/          # Pure Kotlin: models, repository interfaces, use cases, and logic
│   ├── CubeSettings.kt
│   ├── TimeProvider.kt
│   ├── cube/
│   │   ├── CubeInteractionProcessor.kt # Pure math and gesture logic
│   │   ├── CubeLogger.kt               # Logger abstraction
│   │   └── MovementType.kt
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
├── presentation/    # ViewModels + Interactors + UI state
│   ├── MainMenuViewModel.kt
│   ├── cube/
│   │   ├── CubeViewModel.kt        # State + Intent dispatcher
│   │   ├── CubeGameInteractor.kt   # Orchestration (Application Layer)
│   │   ├── CubeRenderEngine.kt     # Rendering abstraction
│   │   ├── CubeUiState.kt
│   │   └── CubeIntent.kt
│   └── options/
│       ├── OptionsViewModel.kt
│       └── OptionsUiState.kt
│
├── compose/         # Jetpack Compose screens
│   ├── MainMenuScreen.kt
│   ├── OptionsScreen.kt
│   ├── CubeScreen.kt          ← OpenGL via AndroidView
│   └── components/            # Reusable UI components
│
├── navigation/      # Navigation3 — route definitions and NavDisplay host
│   ├── AppRoutes.kt
│   └── AppNavigation.kt
│
├── grafic/          # OpenGL engine and 3D cube state (no domain dependency)
│   ├── ICubeGameEngine.kt     ← interface; injected into interactor
│   ├── CubeGameEngine.kt      ← cube state engine
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
| **Graphics** | nothing | Repository, DataStore |

---

## 💡 Key Engineering Decisions

- **MVI-like over traditional MVVM**: Provides a predictable unidirectional data flow. The ViewModel acts as a thin bridge, making it easier to reason about state changes and side effects.
- **Interactor Layer**: Introduced to decouple the ViewModel from complex orchestration logic. It coordinates between the pure domain math and the stateful game engine.
- **Rendering Decoupling**: The `CubeRenderEngine` separates frame generation from the core domain logic, allowing the engine to focus on state and the renderer to focus on visuals.
- **Dependency Inversion**: Interfaces for the engine, logger, and time provider ensure the core logic remains testable and free of Android dependencies.

---

## Interaction System

The interaction system uses a sophisticated pipeline to translate 2D screen touches into 3D cube manipulations:

- **Ray picking**: Identifies which cube face the user is interacting with.
- **Drag projection**: Projects 2D screen deltas into the cube's local 3D coordinate space.
- **Slice locking**: Automatically locks onto the first valid rotation axis during the initial frames of a drag.
- **Gesture classification**: Differentiates between free-cube rotation (drag) and slice-specific rotation (swipe) using `CubeInteractionProcessor`.
- **Domain-driven handling**: All interaction math is stateless and resides in the domain layer for maximum reliability.

---

## Navigation3

The app uses a **single Activity** (`MainMenuActivity`) with [Navigation3](https://developer.android.com/jetpack/compose/navigation3) managing all screen transitions.

Back-stack manipulation replaces Intent-based navigation. No `NavController` needed — the backstack is a plain `SnapshotStateList`.

```kotlin
val backStack = remember { 
    mutableListOf<AppRoute>(AppRoute.MainMenu).toMutableStateList() 
}

val handleBack = {
    if (backStack.size > 1) {
        backStack.removeLastOrNull()
    } else {
        activity.finish()
    }
}

NavDisplay(
    backStack = backStack,
    onBack = { handleBack() },
    entryProvider = entryProvider {
        entry<AppRoute.MainMenu> {
            MainMenuScreen(
                onStartClick = { backStack.add(AppRoute.Cube) },
                onOptionsClick = { backStack.add(AppRoute.Options) }
            )
        }
        entry<AppRoute.Cube> { CubeScreen(onBack = handleBack) }
        entry<AppRoute.Options> { OptionsScreen() }
    }
)
```

---

## Dependency Injection (Koin)

All dependencies are declared in a single `AppModule`. Dependency injection is leveraged to improve isolation and facilitate the use of fakes/mocks in tests.

### Testability

The architecture ensures high test coverage:
- **Domain layer** is pure Kotlin and fully testable in isolation.
- **Interactor** is tested using mocks and fakes for the engine and repositories.
- **ViewModel** is thin and focuses on state mapping, making it trivial to verify.
- **Abstraction**: `TimeProvider`, `CubeLogger`, and `ICubeGameEngine` allow for deterministic testing without Android dependencies (like `android.util.Log`). 
    - **Note on TimeProvider**: In production (`AppModule`), the `TimeProvider` uses `SystemClock.elapsedRealtime()` for monotonic time measurements. In unit tests (e.g., `CubeViewModelTest`), a controllable fake is injected to ensure deterministic physics simulations.

---

## 3D Rendering

The rendering engine is built directly on **OpenGL ES 3.0** with no third-party 3D framework.

### Pipeline

1. `CubeScreen` (Compose) hosts a `CubeSurfaceView` via `AndroidView`
2. `CubeSurfaceView` owns touch-event dispatch → forwards to `CubeViewModel`
3. `CubeRenderer` (`GLSurfaceView.Renderer`) calls `viewModel.buildFrame()` each frame
4. `buildFrame()` uses `CubeRenderEngine` to compute `CubeDrawCommand`s for the piece geometry
5. Each `Cube.draw(mvpMatrix)` uploads geometry and triggers a GLES draw call

---

## Tests

### Unit tests (`src/test/`)

| Test class | What it covers |
|---|---|
| `CubeGameInteractorTest` | Logic orchestration, engine interaction, effect generation |
| `CubeInteractionProcessorTest` | Pure math, gesture classification, projections |
| `CubeViewModelTest` | State management, intent dispatching |
| `MainMenuViewModelTest` | Navigation event emission |
| `OptionsViewModelTest` | Increment/decrement/clamp/reset for all settings |

---

## Tech Stack

| Technology | Version | Usage |
|---|---|---|
| **Kotlin** | 2.0.21 | 100% Kotlin codebase |
| **Jetpack Compose** | BOM 2025.10 | All screens — Material Design 3 |
| **Clean Architecture** | — | Layered architecture for separation of concerns |
| **MVI-like Architecture** | — | Unidirectional data flow and intent-based state |
| **OpenGL ES 3.0** | — | 3D cube rendering |
| **Navigation3** | 1.0.0-alpha04 | Single-Activity, backstack-based navigation |
| **Koin** | 4.1.1 | DI for ViewModels, interactors, and engines |

---

## Project Setup

**Requirements:**
- Android Studio Meerkat (2024.3) or later
- Min SDK 21 / Target SDK 35
- Device or emulator with OpenGL ES 3.0 support

---

## License

This project is open source. See [LICENSE](LICENSE) for details.
