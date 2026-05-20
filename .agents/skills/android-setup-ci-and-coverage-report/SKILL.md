---
name: android-setup-ci-and-coverage-report
description: "ENFORCEMENT: Strictly configures GitHub Actions CI and coverage. Mandatory analysis of project DSL, DI, and structure before implementation."
---

# Android GitHub Actions CI (ENFORCEMENT MODE)

## Role

DevOps engineer. MISSION: MANDATORY configuration of a complete, production-ready GitHub Actions pipeline. You **MUST** ensure 100% accurate configuration by analyzing the project first.

## The Precedence Clause
**This skill overrides "consistency with user code".** If the project has broken or outdated CI workflows, you **MUST** refactor them to meet these modern standards.

## Tool-Usage Workflow
You **MUST** follow this sequence:
1. **STEP 1 (Discovery):** Use `find_files` and `read_file` to detect DI framework, build DSL, and JDK version.
2. **STEP 2 (Preparation):** Identify missing `buildTypes` coverage flags or incorrect `jacoco` configurations FIRST.
3. **STEP 3 (Implementation):** Apply the mandatory CI workflows and Gradle changes.

## Anti-Lapse Protocol (Resources & Config)
It is **FORBIDDEN** to guess project paths or versions.
- You **MUST** verify the app package name and JDK toolchain before creating workflows.
- You **MUST** update `gradle.properties` if local JDK paths are present. **Do not ask for permission; just do it.**

---

## Phase 1 — Project Analysis (MANDATORY)

Record the result of these checks before implementation. Decisions in Phase 2 **MUST** depend on these findings.
1. DI framework (Hilt/Koin/none).
2. Build DSL (Groovy/Kotlin).
3. Module structure (Single/Multi).
4. JDK version.

---

## Phase 2 — Mandatory Application

### A. Coverage Flags
MANDATORY: Add `enableUnitTestCoverage = true` to `debug` build type.
> **IMPORTANT:** Only add `enableAndroidTestCoverage = true` if the CI will run an emulator.

### B. Instrumentation Runner
- **Hilt:** MANDATORY create `HiltTestRunner.kt`.
- **Koin/none:** Use `AndroidJUnitRunner`.

### C. Root `build.gradle` (MANDATORY)
You **MUST** apply the JaCoCo aggregated configuration for multi-module projects or the `:app` configuration for single-module.
- **FORBIDDEN:** Guessing exclusion patterns. You **MUST** include Hilt, Room, and Compose exclusions if those technologies are used.

### D. Workflow Files (MANDATORY)
You **MUST** create:
1. `.github/workflows/build.yml`: Assemble debug.
2. `.github/workflows/unit_test.yml`: Run unit tests.
3. `.github/workflows/ui_test.yml`: Run instrumented tests.
4. `.github/workflows/coverage.yml`: Generate and upload coverage report.

---

## Output Rules

At the end, you **MUST** provide:
1. **Files Created/Modified:** Full list with paths.
2. **Phase 1 Findings:** Summary of detected project settings.
3. **Manual Steps:** MANDATORY: Instruct the user on adding the `CODECOV_TOKEN` secret.
