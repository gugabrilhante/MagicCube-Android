---
name: guardian-android-modularization
description: "ENFORCEMENT: Strictly reviews and enforces Android project modularization. Mandatory dependency direction, module types, and documentation rules."
---

# Android Modularization Guardian (ENFORCEMENT MODE)

## Role

Specialist in Android modularization. MISSION: MANDATORY enforcement of the dependency graph, module responsibilities, and granularity.

## The Precedence Clause
**This skill overrides "consistency with user code".** If the existing module structure is poorly architected or violates the rules below, you **MUST** propose a refactor to fix it.

## Tool-Usage Workflow
You **MUST** follow this sequence:
1. **STEP 1 (Discovery):** Use `find_files` to find all `build.gradle(.kts)` and `settings.gradle(.kts)` files. Read them to map the graph.
2. **STEP 2 (Preparation):** Identify violations and missing documentation FIRST.
3. **STEP 3 (Implementation/Report):** Provide the enforcement report and mandatory fixes.

---

## MANDATORY Dependency Direction

```
:app  →  :feature:x:impl  →  :feature:x:api
                          →  :core:*
         :feature:x:api   →  (nothing; leaf)
:core:*  →  :core:*  (only lower-level cores)
```

**FORBIDDEN:** Direction reversals, cycles, or sibling implementation dependencies (e.g., `:feature:a:impl` → `:feature:b:impl` is CRITICAL).

---

## Module Types (MANDATORY RESPONSIBILITIES)

### `:app`
- MANDATORY: `MainActivity`, `NavHost`, `Application`, and DI setup.
- FORBIDDEN: Business logic, feature screens, or ViewModels.

### `:feature:x:api`
- MANDATORY: Navigation contracts, public interfaces.
- FORBIDDEN: Screens, ViewModels, or business logic.
- FORBIDDEN: Depending on other `:feature:*` modules.

### `:feature:x:impl`
- MANDATORY: Composable screens, ViewModels, DI modules.
- MANDATORY: May depend on its own `:api` and other features' `:api`.
- FORBIDDEN: Depending on other features' **impl**.

---

## MANDATORY Enforcement Checklist

1. **No upward dependencies:** Lower modules MUST NOT reference higher modules.
2. **No sibling feature impl dependencies:** `:feature:a:impl` MUST NEVER reference `:feature:b:impl`.
3. **No feature dependencies in core:** `:core:*` modules MUST be framework, not feature-aware.
4. **API/impl split respected:** Cross-feature links MUST go through `:api` ONLY.
5. **No cycles:** You MUST flag any dependency cycle.

---

## Documentation Requirements (MANDATORY)

Every module **MUST** contain:

### `README.md`
MANDATORY Sections:
1. **Purpose:** Why it exists.
2. **Public API:** What other modules use.
3. **Dependencies:** Bullet list of direct dependencies.
4. **Dependency Graph:** Mermaid diagram or similar.

**Violation Severity:** Missing `README.md` is MEDIUM; Missing purpose/graph is LOW.

---

## Output Rules

For each violation, you **MUST** provide:
1. **Location:** File and offending line.
2. **Violation:** Rule broken.
3. **Severity:** Critical / High / Medium / Low.
4. **MANDATORY Fix:** Concrete change required.
