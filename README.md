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

A custom color scheme built around a **dark navy + cyan/amber** game palette. The palette was designed to complement the `AnimatedBackground` gradient and work in both light and dark themes.

```kotlin
// Dark scheme — primary game experience
private val DarkColorScheme = darkColorScheme(
    primary          = CyanAccent300,      // sky blue accent
    secondary        = AmberAccent,        // warm cube colour
    surface          = SurfaceDark,        // navy card surface
    background       = Navy900,
    onBackground     = OnNavy,
)
```

Typography uses `FontFamily.Cursive` for the game title (`displaySmall`) and the standard Material 3 scale for all other text — ensuring consistent hierarchy without breaking the game aesthetic.

### Custom Shared-Element Transition

Because Navigation3's `NavDisplay` doesn't expose its internal `AnimatedContentScope` for shared elements, a custom overlay system was built from scratch:

**Architecture**
```
AppNavigation
├── CompositionLocalProvider(LocalCubeTransition = CubeTransitionState)
│   └── Box (fillMaxSize)
│       ├── NavDisplay                   ← screen content, z = 0
│       └── CubeTransitionOverlay        ← animated clone, z = 99
```

**`CubeTransitionState`** is shared via `CompositionLocal`. Both screens write their cube bounds via `onGloballyPositioned`. When triggered, `play()` / `playReverse()` animates `progress` from 0 → 1 using `Animatable`.

**Forward transition (MainMenu → Options):**
- Overlay cube traces a **parabolic arc** from the 200 dp source to the 56 dp target
- **Size keyframes** create an overshoot spring: source → target × 1.20 (75 %) → target × 0.92 (90 %) → exact target (100 %)
- **Crossfade handoff** (65–90 %): overlay fades out while the real mini-cube in OptionsScreen fades in — the two composables are never visible simultaneously

**Reverse transition (Options → MainMenu):**
- Endpoints are swapped; the mini-cube flies back and grows with the same overshoot spring
- OptionsScreen card content fades out in the first 50 % of the animation ("taking the settings with it")
- MainMenu large cube stays hidden until the overlay arrives, then crossfades in

```
Position (arc, both directions):
  currentCx = lerp(srcCx, tgtCx, p)
  currentCy = lerp(srcCy, tgtCy, p) − sin(p · π) · 80dp   ← upward arc peak at p=0.5

Size (overshoot spring):
  0 % → 75 %  : lerp(srcSize, tgtSize + 12dp, p/0.75)
  75 % → 90 % : lerp(tgtSize + 12dp, tgtSize − 6dp, ...)  ← bounce
  90 % → 100 %: lerp(tgtSize − 6dp, tgtSize, ...)          ← settle
```

### Route-Aware Navigation Transitions

`NavDisplay.transitionSpec` branches on `initialState.key` / `targetState.key` to give each route pair its own motion:

| Route pair | Exit | Enter |
|---|---|---|
| MainMenu → Options | fade + spring-scale out | spring-scale in (0.90×, damping 0.72) |
| Options → MainMenu | fade + spring-scale out | spring-scale in |
| **MainMenu → Cube** | manual Animatable | `EnterTransition.None` |
| **Cube → MainMenu** | `ExitTransition.None` | scale from 0 + fade (380 ms) |

**MainMenu → Cube** required a special approach. `GLSurfaceView` renders on a dedicated hardware surface composited by the window manager — completely outside the Compose/View alpha hierarchy. No `graphicsLayer` or `NavDisplay` transition can fade it. The solution:

1. Two independent `Animatable`s drive MainMenu's **fade** (220 ms) and **scale collapse** (360 ms) concurrently, at different rates so both effects are visually distinct.
2. Only after `scaleJob.join()` (360 ms) does `backStack.add(AppRoute.Cube)` run — the GL surface appears on an already-dark screen.
3. Inside `CubeScreen`, a pure-Compose `Box` overlay (matching `AnimatedBackground`'s dark colour) starts fully opaque and fades to transparent over 480 ms, revealing the GL scene smoothly.

```
t =   0 ms  MainMenu: fade starts (220 ms) + collapse starts (360 ms)
t = 220 ms  MainMenu fully transparent; ghost still shrinking
t = 360 ms  Collapse done → navigate; CubeScreen GL surface + dark overlay appear
t = 360 ms  Overlay fades out (480 ms)
t = 840 ms  GL scene fully revealed
```

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

Back-stack manipulation replaces Intent-based navigation. No `NavController` needed — the backstack is a plain `SnapshotStateList`.

```kotlin
val backStack = remember {
    mutableListOf<AppRoute>(AppRoute.MainMenu).toMutableStateList()
}

NavDisplay(
    backStack = backStack,
    onBack = { handleBack() },
    transitionSpec = {
        val from = initialState.key
        val to   = targetState.key
        when {
            from is AppRoute.MainMenu && to is AppRoute.Cube ->
                EnterTransition.None togetherWith ExitTransition.None  // manual anim
            from is AppRoute.Cube && to is AppRoute.MainMenu ->
                (fadeIn(tween(380)) + scaleIn(tween(380), 0f)) togetherWith ExitTransition.None
            else ->
                (fadeIn(tween(300)) + scaleIn(CubeArrivalSpring, 0.90f)) togetherWith
                    (fadeOut(tween(220)) + scaleOut(tween(220), 1.06f))
        }
    },
    entryProvider = entryProvider {
        entry<AppRoute.MainMenu> { MainMenuScreen(...) }
        entry<AppRoute.Cube>     { CubeScreen(onBack = handleBack) }
        entry<AppRoute.Options>  { OptionsScreen(onBack = optionsBack) }
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

## Tests

### Unit tests (`src/test/`)

| Test class | What it covers |
|---|---|
| `CubeGameInteractorTest` | Logic orchestration, engine interaction, effect generation |
| `CubeInteractionProcessorTest` | Pure math, gesture classification, projections |
| `CubeViewModelTest` | State management, intent dispatching |
| `MainMenuViewModelTest` | Navigation event emission |
| `OptionsViewModelTest` | Increment/decrement/clamp/reset for all settings |
| `SettingsFlowIntegrationTest` | End-to-end DataStore persistence |

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
