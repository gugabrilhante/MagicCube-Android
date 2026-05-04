# Magic Cube Android

[![Build](https://github.com/gugabrilhante/MagicCube-Android/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/gugabrilhante/MagicCube-Android/actions/workflows/build.yml)
[![Unit Tests](https://github.com/gugabrilhante/MagicCube-Android/actions/workflows/unit_test.yml/badge.svg?branch=master)](https://github.com/gugabrilhante/MagicCube-Android/actions/workflows/unit_test.yml)
[![UI Tests](https://github.com/gugabrilhante/MagicCube-Android/actions/workflows/ui_test.yml/badge.svg?branch=master)](https://github.com/gugabrilhante/MagicCube-Android/actions/workflows/ui_test.yml)
[![codecov](https://codecov.io/gh/gugabrilhante/MagicCube-Android/branch/master/graph/badge.svg)](https://codecov.io/gh/gugabrilhante/MagicCube-Android)

An interactive 3D Rubik's Cube simulator for Android, built with Clean Architecture + MVVM with MVI-like unidirectional data flow, Jetpack Compose, Koin, Navigation3, and OpenGL ES 3.0.

## Available on Google Play

[<img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" width="200">](https://play.google.com/store/apps/details?id=gustavo.brilhante.magiccube2)

---

## Demo

| <img src="docs/magic_cube_options.gif" width="45%"> | <img src="docs/magic_cube_gameplay.gif" width="45%"> |

> Rotate the cube freely with drag gestures. Swipe to rotate individual face slices. Shuffle and solve at your own pace.

---

## Features

- **3D Rendering** via OpenGL ES 3.0 with MVP matrix transformations
- **Touch interaction**: drag to rotate the cube freely, swipe to rotate a face slice
- **Inertia physics**: smooth momentum-based rotation that decays naturally
- **Closest-face detection**: swipe automatically targets the face nearest to the screen
- **Shuffle**: configurable number of random rotations (0–10 moves); 0 starts the cube already solved
- **Settings**: tune shuffle count, rotation speed, and cube size
- **Material 3 design system**: game-inspired dark palette, full typography scale, dynamic color support
- **Custom shared-element transition**: the cube face travels between screens with arc trajectory and spring-overshoot landing
- **Route-aware navigation transitions**: each route pair has its own enter/exit animation
- **Multilingual**: English, Portuguese (Brazil), and Spanish

---

## Architecture

The project follows **Clean Architecture + MVVM with MVI-like unidirectional data flow**, organized into layers that prioritize a single source of truth and clear separation of concerns.

### High-level Flow
```
UI (Compose) → ViewModel (intent dispatcher) → Interactor (application layer)
→ Domain (interaction logic) → Engine (cube state)
→ RenderEngine (frame generation) → Renderer (OpenGL)
```

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
├── compose/         # Jetpack Compose screens and UI components
│   ├── MainMenuScreen.kt
│   ├── OptionsScreen.kt
│   ├── CubeScreen.kt              ← OpenGL via AndroidView + entrance overlay
│   ├── AnimatedBackground.kt      ← Infinite gradient transition
│   ├── CubeSharedTransition.kt    ← Custom shared-element system
│   ├── CollapsibleCard.kt
│   ├── MagicCubeButton.kt
│   └── MagicCubeCard.kt
│
├── navigation/      # Navigation3 — route definitions and NavDisplay host
│   ├── AppRoutes.kt
│   └── AppNavigation.kt           ← Route-aware transitions
│
├── grafic/          # OpenGL engine and 3D cube state (no domain dependency)
│   ├── ICubeGameEngine.kt         ← interface; injected into interactor
│   ├── CubeGameEngine.kt          ← cube state engine
│   ├── CubeSurfaceView.kt         ← custom GLSurfaceView with touch dispatch
│   ├── CubeRenderer.kt
│   ├── Cube.kt
│   ├── CubeShader.kt
│   └── MatrixTracker.kt
│
├── activity/ui/theme/             # Material 3 design tokens
│   ├── Color.kt                   ← Game-inspired dark palette
│   ├── Type.kt                    ← Full M3 typography scale
│   └── Theme.kt                   ← Dark/light color schemes
│
├── di/
│   └── AppModule.kt
│
└── activity/
    └── MainMenuActivity.kt        ← single-Activity entry point
```

### Layer rules

| Layer | May depend on | Must NOT depend on |
|---|---|---|
| **Domain** | nothing | Android SDK, Retrofit, Room, Koin |
| **Data** | Domain | Presentation, Graphics |
| **Presentation** | Domain, Graphics (ICubeGameEngine) | Data (only through interfaces), Android Views |
| **Navigation** | Presentation, Compose | Data, Graphics |
| **Graphics** | Android SDK, OpenGL ES | Repository, DataStore |

---

## 💡 Key Engineering Decisions

- **MVI-like over traditional MVVM**: Provides a predictable unidirectional data flow. The ViewModel acts as a thin bridge, making it easier to reason about state changes and side effects.
- **Interactor Layer**: Introduced to decouple the ViewModel from complex orchestration logic. It coordinates between the pure domain math and the stateful game engine.
- **Rendering Decoupling**: The `CubeRenderEngine` separates frame generation from the core domain logic, allowing the engine to focus on state and the renderer to focus on visuals.
- **Dependency Inversion**: Interfaces for the engine, logger, and time provider ensure the core logic remains testable and free of Android dependencies.

---

## UI & Animation System

The UI was designed following **Material Design 3** guidelines, inspired by the Now in Android reference app, and extended with custom game-feel animations.

### Material 3 Design System

A custom color scheme built around a **dark navy + cyan/amber** game palette, designed to complement the `AnimatedBackground` gradient. Typography uses `FontFamily.Cursive` for the game title and the standard Material 3 scale elsewhere.

### Custom Shared-Element Transition

A custom overlay system built from scratch to handle transitions that Navigation3 doesn't natively expose. It uses `CompositionLocal` and `Animatable` to trace a parabolic arc from the main menu to the options screen, with size keyframes that create an overshoot spring effect.

### Route-Aware Navigation Transitions

`NavDisplay.transitionSpec` defines specific motion for each route pair, such as spring-scale and fade animations. The transition from MainMenu to the 3D CubeScreen uses a coordinated fade and scale-collapse to smoothly reveal the OpenGL surface.

### Micro-interactions & Polish

- **Button press scale**: `MutableInteractionSource` + `animateFloatAsState` shrinks buttons to 96 % on press with a spring
- **Slider value badge**: `animateIntAsState` with spring so the numeric label bounces when the value changes
- **Collapsible card**: `expandVertically + fadeIn` / `shrinkVertically + fadeOut` with chevron rotation
- **Cube face preview**: `animateColorAsState` with staggered delay per cell (333 ms offset) creates a wave effect when the cube's face colors update

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

Back-stack manipulation replaces Intent-based navigation. The backstack is a plain `SnapshotStateList<AppRoute>` — no `NavController` needed. `NavDisplay` handles route-aware enter/exit transitions for each route pair (MainMenu ↔ Options, MainMenu → Cube, Cube → MainMenu).

---

## Dependency Injection (Koin)

All dependencies are declared in a single `AppModule`. Dependency injection is leveraged to improve isolation and facilitate the use of fakes/mocks in tests.

### Testability

The architecture ensures high test coverage:
- **Domain layer** is pure Kotlin and fully testable in isolation.
- **Interactor** is tested using mocks and fakes for the engine and repositories.
- **ViewModel** is thin and focuses on state mapping, making it trivial to verify.
- **Abstraction**: `TimeProvider`, `CubeLogger`, and `ICubeGameEngine` allow for deterministic testing without Android dependencies.

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

## Quality Engineering & Test Strategy

### Test Pyramid

```text
         ┌─────────────────┐
         │   UI Tests      │  AppNavigationTest, OptionsScreenTest, MainMenuScreenTest
         │  (Espresso /    │  Real Activity, real Compose, no OpenGL
         │   Compose)      │
         ├─────────────────┤
         │ Integration     │  DataStoreSettingsDataSourceTest, SettingsFlowIntegrationTest
         │   Tests         │  Real DataStore, fake repository, full vertical slice
         ├─────────────────┤
         │   Unit Tests    │  Pure JVM — JUnit4 + MockK + Turbine
         │                 │  Domain math, orchestration, ViewModel state
         └─────────────────┘
```

### Unit Tests (`src/test/`)

| Test class | What it covers |
|---|---|
| `CubeInteractionProcessorTest` | Pure gesture math: swipe classification, coordinate projection |
| `CubeGameInteractorTest` | Touch orchestration, effect generation (MockK) |
| `CubeViewModelTest` | Intent dispatch, state updates, inertia trigger |
| `MainMenuViewModelTest` | Delayed navigation event emission |
| `OptionsViewModelTest` | Increment/decrement/clamp/reset for all settings |
| `ObserveSettingsUseCaseTest` | Use-case delegation and Flow correctness |
| `SaveSettingsUseCaseTest` | Persistence delegation |
| `SettingsRepositoryTest` | Repository contract with fake data source |
| `SettingsFlowIntegrationTest` | End-to-end: repository → use cases → ViewModel |

### Integration Tests (`src/androidTest/data/`)

`DataStoreSettingsDataSourceTest` — real DataStore on device context, validates the full lifecycle:
`default values → save → observe → update → reset`

### UI Tests (`src/androidTest/ui/`)

Compose UI tests with `createAndroidComposeRule` / `createComposeRule` — no OpenGL, no Koin:
- App launch → MainMenu visible
- Navigate to Options → back navigation
- Slider interaction → UI reflects change
- All screens render expected labels and buttons

### CI/CD

| Workflow | Trigger | Runner |
|---|---|---|
| **Build** | PR → master | ubuntu-latest |
| **Unit Tests** | PR → master | ubuntu-latest |
| **UI Tests** | PR → master | macos-latest (emulator API 29) |
| **Coverage** | push → master | ubuntu-latest (JDK 21) |

Coverage reports are generated with **JaCoCo** (HTML + XML) and uploaded to **Codecov**. OpenGL, Compose UI, and Activity layers are excluded from coverage metrics.

---

## Tech Stack

| Technology | Version | Usage |
|---|---|---|
| **Kotlin** | 2.2.10 | 100% Kotlin codebase |
| **Jetpack Compose** | BOM 2026.03.01 | All screens — Material Design 3 |
| **Clean Architecture** | — | Layered architecture with clear dependency rules |
| **MVI-like Architecture** | — | Unidirectional data flow and intent-based state |
| **OpenGL ES 3.0** | — | 3D cube rendering |
| **Navigation3** | 1.1.0 | Single-Activity, backstack-based navigation |
| **Koin** | 4.2.1 | DI for ViewModels, interactors, and engines |
| **DataStore** | 1.2.1 | Persistent settings storage |
| **AGP** | 9.1.0 | Build tooling with R8 full mode enabled |

---

## Project Setup

**Requirements:**
- Android Studio Meerkat (2024.3) or later
- Min SDK 23 / Target SDK 35
- Device or emulator with OpenGL ES 3.0 support

---

## License

This project is open source. See [LICENSE](LICENSE) for details.
