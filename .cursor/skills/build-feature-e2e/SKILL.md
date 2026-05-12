---
name: build-feature-e2e
description: Builds an entire feature end-to-end for autonomous demo preparation with planning, implementation, critical-path tests, and targeted UI smoke testing. Use only when the user explicitly wants a Cursor Cloud Agent or long-running agent to deliver a working demo-ready feature quickly and safely.
disable-model-invocation: true
---

# Build Feature E2E

## Goal

Autonomously deliver a demo-ready vertical slice: implemented behavior, critical-path tests, targeted verification, and a concise handoff summary.

## Start Here

1. Use this skill only for explicit autonomous demo-prep requests. For ordinary feature requests, use the standard feature-building workflow.
2. Do not switch into Plan mode or wait for human approval.
3. Restate the feature in one sentence.
4. Define the demo-critical path: the shortest user flow that proves the feature works.
5. Target a 15-minute first vertical slice. This is a checkpoint, not a hard stop: if more time is needed, narrow scope and keep working in 15-minute loops until the demo path is verified or honestly blocked.
6. Identify whether this is a frontend feature, backend feature, full-stack feature, or internal-code feature.
7. Proceed autonomously unless blocked by missing credentials, destructive actions, ambiguous product requirements that would change the demo outcome, or an external manual step.

## Autonomous Planning

Before editing, write a compact plan for yourself:

- Success criteria.
- Files and systems likely to change.
- Critical path tests to add or update.
- Verification commands and, for frontend work, the UI smoke path.
- Risks that could make the demo fail.

Then review the plan as if you were reviewing another engineer's proposal:

- Is the vertical slice small enough to finish?
- Does it prove the requested feature end-to-end?
- Are tests focused on the critical path?
- Is any planned work speculative or unrelated?

Revise the plan once, then execute. Do not ask the user to approve the plan.

## Safety Boundaries

- Preserve existing user changes. Check the working tree before risky edits and do not revert unrelated files.
- Do not run destructive commands, touch secrets, modify production data, or change external services without explicit user approval.
- Do not commit or push unless the user explicitly asks.
- If the requested outcome requires credentials, paid external actions, destructive migrations, or manual setup, stop and report the blocker.

## Execution Loop

1. Read the immediate implementation area, public contracts, data flow, and nearby tests.
2. Implement the smallest complete path first. Defer polish until the core path works.
3. Add tests for the critical path and the highest-risk edge case.
4. Run targeted tests first. Add typecheck or broader tests only when contracts or shared code changed.
5. If this is a frontend feature, run the `ui-smoke-test` agent or perform an equivalent targeted browser smoke test against the changed UI path: load the page, exercise the demo flow, check console errors, and check failed network requests.
6. Fix failures that block the demo-critical path. Do not chase unrelated failures unless they were introduced by the change.
7. If the full feature is not reachable quickly, choose a smaller credible path before stopping. Stop with a handoff only when the demo path is verified or no honest vertical slice remains.

## Timebox Discipline

- Prefer a working vertical slice over a broad incomplete implementation.
- Optimize for a 15-minute first pass. At each 15-minute checkpoint, either verify the demo path, narrow the slice, or name the blocker.
- Do not refactor unrelated code for aesthetics.
- If stuck for more than 10 minutes on one issue, choose a smaller path or summarize the blocker only if autonomous progress is no longer possible.
- Do not run the entire test suite unless targeted verification passes and time remains.

## Demo Definition Of Done

- The demo-critical path works.
- Critical-path tests exist and pass.
- Frontend changes have a targeted UI smoke result, including console and network observations.
- Any skipped verification is named with the reason.
- The final response includes exactly what to demo and the primary changed areas.

## Final Handoff

Use this format:

```markdown
## Demo-Ready Feature
[One paragraph describing what works now.]

## Changed Areas
- [Main files or systems touched]

## Critical Path
- [Step 1]
- [Step 2]
- [Step 3]

## Verification
- `[command]`: [result]
- UI smoke: [result or "Not applicable"]

## What To Demo
1. [Action]
2. [Expected result]

## Caveats
- [Skipped check, known limitation, or "None found"]
```
