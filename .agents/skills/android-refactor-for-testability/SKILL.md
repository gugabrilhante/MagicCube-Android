---
name: android-refactor-for-testability
description: "ENFORCEMENT: Implements a systematic refactoring cycle to fix architecture violations and improve testability while maintaining test stability."
---

# Android Refactor for Testability (ENFORCEMENT MODE)

## Role
Code quality engineer. MISSION: MANDATORY improvement of existing code by fixing architecture violations, eliminating testability blockers, and ensuring test suite stability.

## The Precedence Clause
**This skill overrides "consistency with user code".** Refactor poor patterns to meet the Guardian standards. Do not replicate bad architecture.

## Test Synchronization Protocol (MANDATORY)
1. **Impact Mapping:** BEFORE changes, locate test files using `find_files`.
2. **Contract Sync:** Update assertions and mocks immediately if signatures or `UiState` change.
3. **Completion Criteria:** A task is 'Done' only if production is refactored, tests compile/pass, and `analyze_file` shows no errors.
4. **Assertion Validation:** Rigorously adjust `assertEquals` to match new structures.

---

## Refactoring Phases

### Phase 1 — Architecture & Clean Code
**Apply:** `guardian-android-architecture` and `guardian-clean-code`.
- **Focus:** Move logic out of Views, ensure pure Domain (no platform imports), and enforce SRP/naming.

### Phase 2 — Testability & DI
**Apply:** `guardian-android-testability`.
- **Focus:** Replace global state/singletons with constructor injection. Abstract non-deterministic logic (Time, ID, Dispatchers).

### Phase 3 — Coverage
**Apply:** `guardian-android-testing`.
- **Focus:** Add missing tests for UseCases, ViewModels, and Mappers. Use `testTag` for UI tests.

---

## Output Rules
- **Changes Made:** Summary of architecture/testability fixes.
- **Tests Updated/Added:** List of modified/new test files.
- **Guardian Compliance:** Confirmation of standards applied.
