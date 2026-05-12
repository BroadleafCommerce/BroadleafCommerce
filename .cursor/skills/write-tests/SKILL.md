---
name: write-tests
description: Add or improve tests for changed behavior using the project's existing testing patterns. Use when the user asks to add tests, improve coverage, reproduce a bug, or verify a feature.
---

# Write Tests

## Workflow

1. Read nearby tests and helpers before writing new tests.
2. Identify the intent the test must protect.
3. Prefer extending an existing test file when it keeps related behavior together.
4. Use existing factories, render helpers, fixtures, and assertion style.
5. Add the smallest test that fails for the wrong behavior and passes for the right reason.
6. Run the narrowest matching test command.
7. If the test cannot be run, explain why and provide the exact command to run later.

## Test Quality Bar

- The test name should describe user-visible or contract-level behavior.
- Avoid assertions that simply mirror implementation details.
- Include edge cases only when they are part of the risk introduced by the change.
- Do not create broad snapshots unless the repo already uses them for that surface.

## Closeout

Report the behavior protected, the files touched, and the verification command.
