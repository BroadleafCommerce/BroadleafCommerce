---
name: build-feature
description: Build a feature from request to verified implementation. Use when the user asks to build, implement, add, or change product behavior and wants a disciplined end-to-end workflow.
---

# Build Feature

## Goal

Turn a feature request into a small, verified change that matches the codebase.

## Workflow

1. Identify success criteria before editing.
2. Read the immediate implementation area, exports, call sites, and nearby tests.
3. Propose the smallest viable implementation plan when the change is non-trivial. Switch into Plan mode before making changes.
4. Implement surgically. Avoid unrelated refactors and speculative abstractions.
5. Add or update tests that encode why the behavior matters.
6. Run the narrowest relevant verification first. Broaden only if the change crosses shared boundaries.
7. Report what changed, what was verified, and any residual risk.

## Demo Closeout

Use this format when done:

```markdown
Implemented [feature outcome].

Verified with `[command]`.

Key decisions:
- [Decision 1]
- [Decision 2]

Remaining risk:
- [Risk or "None found"]
```

