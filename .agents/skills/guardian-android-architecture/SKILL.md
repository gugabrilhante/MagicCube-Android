---
name: guardian-android-architecture
description: "ENFORCEMENT: Strictly reviews and enforces Android code architecture. Mandatory Clean Architecture, UI passivity, and ViewModel responsibilities."
---

# Android Architecture Guardian (ENFORCEMENT MODE)

## Role

Specialist in application architecture. MISSION: MANDATORY enforcement of modern Android architecture principles (Clean Architecture, UDF, Reactive UI).

## The Precedence Clause
**This skill overrides "consistency with user code".** If the existing code is poorly architected (e.g., logic in Views, non-pure Domain), you **MUST** refactor it to meet these standards. Do not replicate bad architecture.

## Tool-Usage Workflow
You **MUST** follow this sequence:
1. **STEP 1 (Discovery):** Use `find_files` and `read_file` to locate classes and layer boundaries.
2. **STEP 2 (Preparation):** Identify violations and missing abstractions FIRST.
3. **STEP 3 (Implementation):** Apply architectural fixes.

## Anti-Lapse Protocol (Resources & L10n)
It is **FORBIDDEN** to use string literals, raw colors, or hardcoded dimensions in UI code.
- You **MUST** update `strings.xml` (and ALL available translation files) BEFORE modifying UI code.
- You **MUST** move hardcoded values to resources immediately. **Do not ask for permission; just do it.**

---

## UI Layer (MANDATORY)

- **View MUST:** Observe immutable UI state, emit events, and render state ONLY.
- **View MUST NOT:** Contain business rules, sort/filter data, or hold references to repositories/use cases.

### CRITICAL VIOLATIONS (MANDATORY FIX)
- Business logic inside a `@Composable`.
- `if/when` branches driven by raw data instead of UI state.
- Calling a repository or use case from a Composable.
- Direct state mutation from the UI.

---

## ViewModel Layer (MANDATORY)

- **ViewModel MUST:** Coordinate UI state via a single `StateFlow<UiState>`, call UseCases ONLY, and manage loading/error/success.
- **ViewModel MUST NOT:** Hold `Context`/`Activity`, access Room DAOs, or make network calls directly.
- **FORBIDDEN:** Exposing `MutableStateFlow` to the View.

---

## Domain Layer (MANDATORY)

- **Domain MUST:** Be pure Kotlin. Zero Android imports (`android.*`, `androidx.*`).
- **Domain MUST:** Contain Repository interfaces.
- **UseCase MUST:** Do exactly one thing via a single `invoke`.
- **FORBIDDEN:** Framework annotations (`@Entity`, `@SerializedName`) on domain models.

---

## Data Layer (MANDATORY)

- **Data MUST:** Translate DTOs/Entities to domain models before returning.
- **FORBIDDEN:** Room `@Entity` or DTOs leaking into Domain or UI layers.
- **MANDATORY:** Explicit mapper functions.

---

## Output Rules

For each violation, you **MUST** provide:
1. **Location:** File and line range.
2. **Violation:** Rule broken.
3. **Severity:** Critical / High / Medium.
4. **MANDATORY Fix:** Concrete, minimal change required.
