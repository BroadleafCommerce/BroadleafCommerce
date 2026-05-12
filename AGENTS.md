# AGENTS.md

These rules apply to every task in this project unless explicitly overridden.
Bias: caution over speed on non-trivial work. Use judgment on trivial tasks.

## Service Extraction Boundary

For strangler-style service extractions, name the capability and ownership boundary before coding. The extracted service should own only the scoped capability; Broadleaf remains source of truth for unrelated catalog, cart, checkout, payment, tax, inventory, and persistence concerns unless explicitly included. For the first pilot, shipping estimation means estimate calculation and validation only.

## Rule 1 - Think Before Coding

State assumptions explicitly. If uncertain, ask rather than guess.
Present multiple interpretations when ambiguity exists.
Push back when a simpler approach exists.
Stop when confused. Name what's unclear.

## Rule 2 - Simplicity First

Minimum code that solves the problem. Nothing speculative.
No features beyond what was asked. No abstractions for single-use code.
Test: would a senior engineer say this is overcomplicated? If yes, simplify.

## Rule 3 - Surgical Changes

Touch only what you must. Clean up only your own mess.
Don't improve adjacent code, comments, or formatting.
Don't refactor what isn't broken. Match existing style.

## Rule 4 - Goal-Driven Execution

Define success criteria. Loop until verified.
Don't follow steps blindly. Define success and iterate.
Strong success criteria let you loop independently.

## Rule 5 - Use The Model Only For Judgment Calls

Use the model for classification, drafting, summarization, and extraction.
Do not use the model for routing, retries, or deterministic transforms.
If code can answer, code answers.

## Rule 6 - Token Budgets Are Not Advisory

Per-task: 4,000 tokens. Per-session: 30,000 tokens.
If approaching budget, summarize and start fresh.
Surface the breach. Do not silently overrun.

## Rule 7 - Surface Conflicts, Don't Average Them

If two patterns contradict, pick one: more recent or more tested.
Explain why. Flag the other for cleanup.
Don't blend conflicting patterns.

## Rule 8 - Read Before You Write

Before adding code, read exports, immediate callers, shared utilities, and nearby tests.
"Looks orthogonal" is dangerous. If unsure why code is structured a way, ask.

## Rule 9 - Tests Verify Intent, Not Just Behavior

Tests must encode why behavior matters, not just what it does.
A test that can't fail when business logic changes is wrong.

## Rule 10 - Checkpoint After Every Significant Step

Summarize what was done, what's verified, and what's left.
Don't continue from a state you can't describe back.
If you lose track, stop and restate.

## Rule 11 - Match The Codebase's Conventions, Even If You Disagree

Conformance beats taste inside the codebase.
If you genuinely think a convention is harmful, surface it. Don't fork silently.

## Rule 12 - Fail Loud

"Completed" is wrong if anything was skipped silently.
"Tests pass" is wrong if any were skipped.
Default to surfacing uncertainty, not hiding it.
