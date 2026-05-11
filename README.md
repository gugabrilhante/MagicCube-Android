# Magic Cube Android

[![CI](https://github.com/gugabrilhante/MagicCube-Android/actions/workflows/ci.yml/badge.svg?branch=master)](https://github.com/gugabrilhante/MagicCube-Android/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/gugabrilhante/MagicCube-Android/branch/master/graph/badge.svg)](https://codecov.io/gh/gugabrilhante/MagicCube-Android)

An interactive 3D Rubik's Cube simulator for Android, built with Clean Architecture + MVVM with MVI-like unidirectional data flow, Jetpack Compose, Koin, Navigation3, and OpenGL ES 3.0.

### Quick Navigation
- [Demo](#demo)
- [Key Features](#features)
- [Architecture & Modularization](#architecture)
- [Quality Engineering & Test Strategy](#quality-engineering--test-strategy)
- [Interaction & Graphics](#interaction-system)
- [Tech Stack](#tech-stack)

---

## Available on Google Play

[<img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" width="200">](https://play.google.com/store/apps/details?id=gustavo.brilhante.magiccube2)

---

## Demo

| Gameplay — Interactive 3D Cube | Options — Customization |
|--------------------------|--------------------------|
| <img src="docs/magic_cube_gameplay.gif" width="100%" alt="Magic Cube Gameplay Demo"> | <img src="docs/magic_cube_options.gif" width="100%" alt="Magic Cube Options Demo"> |

> Rotate the cube freely with drag gestures. Swipe to rotate individual face slices. Shuffle and solve at your own pace.

---

## Features

- **3D Rendering** via OpenGL ES 3.0 with MVP matrix transformations.
- **Natural Physics**: Inertia-based rotation that decays naturally for a tactile feel.
- **Intelligent Interaction**: Automatic closest-face detection for intuitive slice swiping.
- **Material 3 Design**: Game-inspired dark palette with dynamic color support and full typography scale.
- **Smooth Transitions**: Custom shared-element system with parabolic arc trajectories and spring-overshoot animations.
- **Multilingual Support**: English, Portuguese (Brazil), and Spanish.

---

## Architecture

The project follows **Clean Architecture + MVVM with MVI-like unidirectional data flow**, prioritizing a single source of truth and clear separation of concerns.

### High-level Flow
```text
UI (Compose) → ViewModel (intent dispatcher) → Interactor (application layer)
→ Domain (interaction logic) → Engine (cube state)
→ RenderEngine (frame generation) → Renderer (OpenGL)
```

### Layer Responsibilities

- **Domain**: Pure Kotlin layer containing business rules, gesture mathematics, and repository interfaces.
- **Data**: Implementation of domain contracts using DataStore for persistent settings.
- **Presentation**: ViewModels and Interactors managing UI state and orchestrating business logic.
- **Graphics**: Stateless OpenGL engine responsible for 3D state and rendering commands.
- **Navigation**: Route definitions and transitions using Navigation3.

### Dependency Rules

| Layer | May depend on | Must NOT depend on |
|---|---|---|
| **Domain** | nothing | Android SDK, Frameworks |
| **Data** | Domain | Presentation, Graphics |
| **Presentation** | Domain, Graphics (Interfaces) | Data, Android Views |
| **Graphics** | Android SDK, OpenGL ES | Domain Logic, Data |

---

## Quality Engineering & Test Strategy

The project employs a robust testing strategy to ensure reliability across all layers, from mathematical projections to UI interactions.

### Test Pyramid

- **Unit Tests (JVM)**: Focuses on core business logic, gesture classification (2D to 3D mapping), and ViewModel state transitions. Uses **MockK** for mocking and **Turbine** for testing Coroutines Flows.
- **Integration Tests**: Validates the persistence layer and the vertical slice from Repository to ViewModel, ensuring data consistency with **Jetpack DataStore**.
- **UI Tests (Compose)**: Verifies user journeys (navigation, settings adjustment) using the Compose Testing library, ensuring the UI remains responsive and correct.

### CI/CD Pipeline

| Workflow | Responsibility | Environment |
|---|---|---|
| **Build & Lint** | Verifies compilation and code style | Ubuntu |
| **Unit Tests** | Runs all JVM tests on every PR | Ubuntu |
| **UI Tests** | Executes instrumentation tests | macOS (Emulator) |
| **Coverage** | Generates JaCoCo reports and uploads to Codecov | Ubuntu |

---

## Interaction System

The interaction system translates 2D screen touches into 3D manipulations through a sophisticated pipeline:
- **Ray Picking**: Identifies the targeted cube face in 3D space.
- **Drag Projection**: Maps screen deltas to the cube's local coordinate system.
- **Gesture Classification**: Uses pure domain math to differentiate between free rotation and specific slice moves.

---

## Tech Stack

| Technology | Version | Usage |
|---|---|---|
| **Kotlin** | 2.2.10 | Core language |
| **Jetpack Compose** | 2026.03.01 | UI Framework |
| **OpenGL ES** | 3.0 | 3D Rendering |
| **Navigation3** | 1.1.0 | Single-Activity navigation |
| **Koin** | 4.2.1 | Dependency Injection |
| **DataStore** | 1.2.1 | Persistence |

---

## Project Setup

**Requirements:**
- Android Studio Meerkat (2024.3)+
- Min SDK 23 / Target SDK 35
- Device/Emulator with OpenGL ES 3.0 support

---

## License

This project is open source. See [LICENSE](LICENSE) for details.
