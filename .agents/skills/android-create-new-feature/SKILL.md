---
name: android-create-new-feature
description: "ENFORCEMENT: Scaffolds a complete feature by orchestrating all Guardian standards. Focuses on workflow and placement."
---

# Android Create New Feature (ENFORCEMENT MODE)

## Role
Android feature architect. MISSION: Scaffold a complete, production-ready feature. You **MUST** ensure full compliance with all Guardian standards to avoid any future refactoring.

## The Precedence Clause
**This skill overrides "consistency with user code".** Implement the new feature using the high standards defined in the referenced Guardian skills, regardless of existing project patterns.

## Tool-Usage Workflow
1. **Discovery:** Use `find_files`/`read_file` to detect DI, modules, and navigation.
2. **Resources:** Update `strings.xml` (all languages) and `colors.xml` FIRST.
3. **Implementation:** Create code and tests following the phases below.

---

## Phase 1 — Structure & Modularization (MANDATORY)

1. **Modularization:** Apply `guardian-android-modularization`. 
   - If multi-module: Create `:feature:<name>:api` and `:feature:<name>:impl`.
2. **Packages:** Apply `guardian-package-architecture`. Use feature-first: `feature/<name>/{data, domain, presentation}`.

---

## Phase 2 — Implementation Standards (MANDATORY)

You **MUST** implement the feature by strictly following these specifications:

1. **Architecture:** Apply `guardian-android-architecture`. 
   - *Key Enforcement:* Pure Domain, UI/Content split, and explicit Mappers.
2. **Testability:** Apply `guardian-android-testability`. 
   - *Key Enforcement:* Constructor injection for all dependencies and dispatchers.
3. **Clean Code:** Apply `guardian-clean-code`. 
   - *Key Enforcement:* Strict naming (no `Manager`/`Data` suffixes) and SRP.

---

## Phase 3 — Testing (MANDATORY)

Apply `guardian-android-testing` to achieve 100% coverage for new logic:
- **Unit:** UseCases, ViewModels, and Mappers.
- **Integration:** Repository/DAO logic.
- **UI:** key journeys using `testTag` matchers.

---

## Output Rules
Provide:
1. **Files Created:** Full list with paths.
2. **DI Wiring:** Summary of bindings added.
3. **Guardian Compliance:** List of Guardian skills applied.
4. **Manual Steps:** Remaining tasks.
