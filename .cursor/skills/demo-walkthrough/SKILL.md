---
name: demo-walkthrough
description: Produce a concise engineer-facing walkthrough of a completed change. Use when preparing to demo Cursor, explain an implementation, or summarize how the agent worked.
disable-model-invocation: true
---

# Demo Walkthrough

## Purpose

Create a short technical narrative that makes the work easy to present to software engineers.

## Inputs To Gather

- User request or issue being solved.
- Important files changed.
- Key design decisions.
- Verification performed.
- Remaining risks or skipped checks.

## Output Format

```markdown
## What Changed
[Two or three sentences focused on user-visible outcome.]

## Why This Shape
[One paragraph explaining the main design choice and how it matches the codebase.]

## Verification
- `[command]`: [result]
- Manual check: [path or flow]

## Demo Script
1. Show the original problem.
2. Show the small set of files Cursor changed.
3. Show the test or browser verification.
4. Call out one place where Cursor preserved existing project conventions.
```

Keep the walkthrough under two minutes when spoken aloud.
