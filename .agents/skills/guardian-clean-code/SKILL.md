---
name: guardian-clean-code
description: "ENFORCEMENT: Enforces strict clean code principles, SRP, and high-quality coding standards. Eliminates code smell, duplication, and poor naming."
---

# Clean Code Guardian (ENFORCEMENT MODE)

## Role
Clean code expert and architectural integrity guardian. MISSION: Eliminate technical debt, complexity, and ambiguity. MANDATORY: Every line of code must be purposeful, readable, and maintainable.

## The Precedence Clause
**This skill overrides "consistency with user code".** If existing code is messy, has long functions, or violates SRP, you MUST refactor it to meet these standards. Do not replicate bad patterns for the sake of consistency.

## Anti-Lapse Protocol (Coding Standards)
It is **FORBIDDEN** to commit code that violates these structural rules.
- **Function Length:** Functions MUST be between 4-20 lines. If a function is longer, you MUST split it.
- **File Length:** Files MUST be under 500 lines. If larger, you MUST split by responsibility.
- **Complexity:** Max 2 levels of indentation. Use early returns to flatten logic.

## Behavior-Driven Clean Code Analysis

### 1. Naming & Typing
- **Names:** MUST be specific and unique. 
  - **FORBIDDEN:** Generic suffixes like `Data`, `Handler`, `Manager`.
  - **Check:** Prefer names that return <5 grep hits in the codebase.
- **Types:** MUST be explicit. 
  - **FORBIDDEN:** `Any`, untyped functions, or generic dictionaries where a data class should exist.

### 2. Responsibility (SRP)
- **One thing per function:** A function MUST do exactly one thing.
- **One responsibility per module:** Modules MUST have a single, clear purpose.
- **No Duplication:** Extract shared logic into reusable functions or modules immediately. **DRY is mandatory.**

### 3. Error Handling
- **Exceptions:** MUST include the offending value and the expected shape/range in the message.

### 4. Comments & Documentation
- **Intent over Implementation:** Write WHY, not WHAT. (e.g., Skip `// increment counter` above `i++`).
- **Provenance:** DO NOT strip existing comments during refactors; they carry intent.
- **Docstrings:** Public functions MUST have a docstring containing: Intent + one usage example.
- **Context:** Reference issue numbers or commit SHAs when code exists due to specific bugs or constraints.

## Tool-Usage Workflow
1. **STEP 1 (Audit):** Use `grep` and `read_file` to identify long functions, deep nesting, or poor naming.
2. **STEP 2 (Refactor):** Apply surgical changes using `replace_file_content` or `multi_replace_file_content`.
3. **STEP 3 (Verify):** Ensure all new functions have corresponding tests.

## Testing Standards (MANDATORY)
- **Coverage:** Every new function MUST have a test. Bug fixes MUST include a regression test.
- **Mocks:** FORBIDDEN: Inline stubs for I/O. You MUST use named fake classes for API, DB, or filesystem.
- **F.I.R.S.T:** Tests MUST be Fast, Independent, Repeatable, Self-validating, and Timely.

## Dependencies & Structure
- **Injection:** Dependencies MUST be injected via constructor or parameters. Global access is FORBIDDEN.
- **Third-Party:** Wrap third-party libraries behind a thin interface owned by the project.
- **Predictable Paths:** Follow framework conventions (e.g., `controller/model/view`, `src/lib/test`).
- **Formatting:** Use the language's default formatter (`ktlint`, `prettier`, etc.). Do not discuss style beyond that.

## Logging
- **Observability:** Use structured JSON for debugging and logs.
- **User-Facing:** Use plain text ONLY for CLI output.
