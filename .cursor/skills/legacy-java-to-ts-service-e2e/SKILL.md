---
name: legacy-java-to-ts-service-e2e
description: Runs an end-to-end Cloud Agent workflow for extracting a named legacy Java capability into a Node.js/TypeScript service. Use when the user wants one-shot modernization from legacy behavior discovery through API contract design, implementation, tests, runtime endpoint smoke, and demo-ready proof without requiring app integration.
---

# Legacy Java To TypeScript Service E2E

## Goal

Deliver a demo-ready vertical slice for one extracted Java capability: source-of-truth analysis, TypeScript service, contract tests, runtime endpoint proof, and an optional note for future adapter/UI integration.

## Start Here

1. Restate the named capability and demo-critical proof path.
2. Apply `service-extraction-boundary`, `legacy-behavior-source`, and `service-contract-testing` rules.
3. Use the narrower `legacy-java-to-ts-service` skill for implementation guardrails.
4. Before editing, summarize likely files/directories to touch and verification commands.

## Required Subagent Sequence

Launch the read-only analysis agents in parallel when possible:

1. `legacy-behavior-investigator`
   - Identify source-of-truth Java classes, tests, behavior rules, and edge cases.
2. `api-contract-designer`
   - Propose endpoints, DTOs, response/error shapes, and example payloads.
3. `test-migration-agent`
   - Map legacy tests to service API/contract tests.
4. `adapter-planner`
   - Use only when the user explicitly asks for adapter work. Otherwise, record what future adapter seam would be likely without implementing it.

Synthesize the results into a short implementation plan, then implement.

## Implementation Loop

1. Create the smallest TypeScript service that proves the extracted capability.
2. Keep business logic modular and independent from Broadleaf entities.
3. Add contract/API tests derived from legacy behavior before broadening scope.
4. Run the narrowest service test command first.
5. Fix only failures related to the extracted capability.
6. Do not move unrelated catalog, cart, checkout, payment, tax, inventory, product, or SKU ownership.

## Proof Order

1. API/contract tests pass.
2. Run `service-runtime-smoke` as the final service proof:
   - Start or verify the local service.
   - Hit health and primary business endpoints.
   - Report status codes and key response fields.
3. If a demo app is involved, verify it only as baseline/context unless the user explicitly asked for adapter integration.
4. Do not claim the demo app is powered by the new service unless an adapter was actually implemented and verified.

## Shipping Pilot Defaults

When the named capability is shipping estimation:

- Source of truth: Broadleaf fulfillment pricing providers and fulfillment pricing tests.
- Service owns: fulfillment/shipping estimate calculation, band validation, and explain responses.
- Broadleaf keeps: catalog, cart, checkout, payment, tax, inventory, products, and SKUs.
- `demosite` role: baseline visual context and final "still works" smoke only.
- Out of scope for the first pilot: wiring `demosite` checkout to the new service.
- Final demo proof: API tests plus endpoint smoke evidence; then show `demosite` still runs separately.

## Final Handoff

Use this format:

```markdown
## Extracted Capability
[Capability and service boundary.]

## Legacy Source Of Truth
- `path`: [behavior covered]

## New Service
- [endpoints and modules]

## Verification
- `[test command]`: [result]
- Runtime smoke: [endpoint status codes and key fields]
- Demo app status: [baseline/still works check, or "Not run"]
- Adapter status: [out of scope, planned future work, or implemented if explicitly requested]

## What To Demo
1. [legacy source or baseline UI]
2. [new service/API tests]
3. [runtime endpoint proof]
4. [app still works separately, if checked]

## Caveats
- [skipped check, blocker, or "None found"]
```
