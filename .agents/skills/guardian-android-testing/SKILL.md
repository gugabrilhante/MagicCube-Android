---
name: guardian-android-testing
description: "ENFORCEMENT: Analyzes coverage and adds missing tests. Enforces stable selectors and Resource-First patterns. Strictly identifies testability blockers."
---

# Android Testing Guardian (ENFORCEMENT MODE)

## Role

Testing expert. MISSION: MANDATORY improvement of testing confidence. You MUST analyze coverage and add missing tests for all layers.

## The Precedence Clause
**This skill overrides "consistency with user code".** If existing tests are flaky, use text matchers, or ignore modern standards, you MUST refactor them to meet these standards.

## Anti-Lapse Protocol (Resources & L10n)
It is **FORBIDDEN** to assert on hardcoded display strings.
- You **MUST** use resource IDs (`R.string.x`) or stable selectors (`testTag`).
- If a `testTag` is missing, you **MUST** add it to the UI code FIRST. **Do not ask for permission; just do it.**

## Tool-Usage Workflow
You MUST follow this sequence:
1. **STEP 1 (Discovery):** Use `find_files` and `read_file` to locate code and existing tests.
2. **STEP 2 (Preparation):** Add missing `testTag`s or resource keys FIRST.
3. **STEP 3 (Implementation):** Write and run tests using the prepared stable selectors.

---

# Phase 1 — Project Audit

Inspect the codebase. MANDATORY: Identify layer boundaries and DI framework (Hilt or Koin).

---

# Phase 2 — Testability Check (ENFORCED)

Before writing tests, you **MUST** inspect the target class.
Look for blockers: Static access, hardcoded dispatchers, framework calls, `Context` in logic, system clock/random access.

If a class is NOT testable:
**STOP.** FORBIDDEN: Writing unreliable tests for untestable code.
**MANDATORY ACTION:** Tell the user: "This class contains testability blockers. You MUST run `guardian-android-testability` before tests can be added."

---

# Phase 3 — Unit Test Audit

Analyze missing tests. MANDATORY coverage:
- **Domain:** UseCases (happy/error/edge).
- **Data:** Repositories, Mappers, Flows.
- **Presentation:** ViewModels (state, events, transitions).

---

# Phase 4 — Integration Tests

MANDATORY for Room:
- Test all CRUD and Flow updates using an **in-memory** database. NO mocks.

---

# Phase 5 — UI / E2E Tests

MANDATORY: Cover critical user journeys.

## Anti-flaky rules (MANDATORY ENFORCEMENT)

FORBIDDEN: Text-based matchers if a stable selector exists.
FORBIDDEN: `Thread.sleep()`.

| Flaky (FORBIDDEN) | Stable (MANDATORY) |
|---|---|
| `onView(withText("Submit"))` | `onView(withId(R.id.btn_submit))` |
| `onNodeWithText("Submit")` | `onNodeWithTag("btn_submit")` |
| `Thread.sleep(2000)` | `IdlingResource` or `waitUntil {}` |

**Compose:** You **MUST** add `.testTag("tag")` to every interactive element.
**View system:** You **MUST** assign `android:id` to all interactive elements.

---

# Final Report

At the end, you **MUST** provide:
1. **Tests Added:** List all new tests.
2. **Coverage Gaps:** Remaining scenarios.
3. **Blocked Classes:** Classes requiring refactoring.
