---
name: legacy-java-to-ts-service
description: Extract legacy Java business capabilities into Node.js/TypeScript services with behavior parity, service-boundary discipline, API contract tests, runtime smoke checks, and optional thin Java adapters. Use when modernizing Broadleaf Java services, providers, workflows, or controllers into a separate TypeScript service.
---

# Legacy Java To TypeScript Service

## Goal

Extract one named Java capability into a focused TypeScript service without expanding ownership beyond the agreed boundary.

## Workflow

1. Name the capability and demo-critical path.
2. Identify Java source-of-truth classes, immediate collaborators, and nearby tests.
3. Define ownership: new service owns, Broadleaf keeps, adapter translates.
4. Extract behavior rules, defaults, error cases, and edge cases.
5. Design service endpoints, DTOs, and response/error shapes.
6. Implement a small TypeScript vertical slice with modular business logic.
7. Translate legacy tests into service API or contract tests.
8. Run API tests first, then runtime endpoint smoke checks.
9. Add or plan a thin Java adapter only after the service contract is proven.
10. If a demo UI is involved, smoke test the UI last.

## Subagent Choreography

Use reusable agents when the task benefits from parallel work:

- `legacy-behavior-investigator`: legacy behavior, edge cases, source files.
- `api-contract-designer`: endpoints, DTOs, examples, error semantics.
- `test-migration-agent`: legacy tests mapped to service tests.
- `service-runtime-smoke`: starts or verifies the service and hits endpoints.
- `adapter-planner`: minimal adapter/facade design.
- `demo-narrative-reviewer`: final customer-facing story and risks.

## Implementation Guardrails

- Keep service logic independent from Broadleaf entities; use DTOs at boundaries.
- Do not move unrelated catalog, cart, checkout, payment, tax, or inventory ownership.
- Prefer explicit validation and deterministic calculations over hidden framework behavior.
- Keep the adapter thin: object translation, HTTP call, timeout/error mapping.
- Preserve legacy behavior covered by tests unless the user approves a behavior change.

## Verification Order

1. Service API tests derived from legacy behavior.
2. Runtime endpoint smoke with concrete request/response evidence.
3. Legacy adapter tests if an adapter was implemented.
4. Demo app smoke check only after contract parity is proven.

## Closeout Format

```markdown
## Extracted Capability
[Capability and new service boundary.]

## Source Of Truth
- [Legacy classes/tests read]

## Contract Proof
- `[command]`: [result]
- Endpoint smoke: [request/response summary]

## Adapter/UI Status
[Implemented, planned, or intentionally skipped.]

## Demo Script
1. Show legacy behavior.
2. Show extracted service contract.
3. Show API tests/runtime smoke.
4. Show app smoke or adapter plan.

## Risks
- [Remaining risk or "None found"]
```
