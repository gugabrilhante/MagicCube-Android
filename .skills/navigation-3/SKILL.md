# Navigation 3

A navigation library for Jetpack Compose that is built on top of the Scene API.

## Core concepts

- *[Scene](references/android/guide/navigation/navigation-3/scene.md)*: A Scene is the fundamental building block of Navigation 3. It represents a single screen or destination in your application.
- *[SceneHost](references/android/guide/navigation/navigation-3/scene-host.md)*: A SceneHost is responsible for managing a stack of Scenes and providing the necessary infrastructure for navigation.
- *[NavWrapper](references/android/guide/navigation/navigation-3/nav-wrapper.md)*: A NavWrapper is used to wrap a Scene and provide additional functionality, such as deep linking or analytics.

## Comparison with Navigation 2

- **Scene API**: Navigation 3 is built on top of the Scene API, which provides a more declarative and flexible way to define your navigation structure.
- **Improved state management**: Navigation 3 offers more robust and flexible state management, making it easier to handle complex navigation scenarios.
- **Declarative API**: The Navigation 3 API is more declarative and intuitive, making it easier to learn and use.
- **Type-safety**: Navigation 3 provides improved type-safety for navigation routes and arguments, reducing the risk of runtime errors.

## Key APIs

- *[NavDisplay](references/android/guide/navigation/navigation-3/nav-display.md)*: A Composable that displays the current Scene in a SceneHost.
- *[rememberNavStack](references/android/guide/navigation/navigation-3/remember-nav-stack.md)*: A Composable function that creates and remembers a NavStack, which represents the current navigation state.
- *[rememberNavWrapper](references/android/guide/navigation/navigation-3/remember-nav-wrapper.md)*: A Composable function that creates and remembers a NavWrapper.

## Migration guide

- *[Navigation 2 to Navigation 3 migration guide](references/android/guide/navigation/navigation-3/migration-guide.md)*: Step-by-step guide to migrate an Android application from Navigation 2 to Navigation 3, covering dependency updates, route changes, state management, and UI component replacements.

### Requirements

- *[Guide: Migrate to type-safe navigation in Compose](references/android/guide/navigation/type-safe-destinations.md)* : Step-by-step guide to migrating an Android application from string-based navigation to **Type-Safe Navigation** in Jetpack Compose using Jetpack Navigation 2.

## Developer documentation

- *[Navigation 3](references/android/guide/navigation/navigation-3/index.md). Search documentation for more information on basics, saving and managing navigation state, modularizing navigation code, creating custom layouts using Scenes, animating between destinations, or applying logic or wrappers to destinations.

## Recipes

Code examples showcasing common patterns.

- *[Navigation 3 basic usage](references/android/guide/navigation/navigation-3/recipes/basic-usage.md)*: Example demonstrating basic setup and usage of Navigation 3 in a Jetpack Compose application.
- *[Navigation 3 with deep linking](references/android/guide/navigation/navigation-3/recipes/deep-linking.md)*: Example showing how to implement deep linking with Navigation 3.
- *[Navigation 3 with custom transitions](references/android/guide/navigation/navigation-3/recipes/custom-transitions.md)*: Example demonstrating how to create and use custom animations between Scenes.
