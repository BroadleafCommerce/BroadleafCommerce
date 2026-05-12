# Shipping Estimator Extraction Prompt

Use the `legacy-java-to-ts-service-e2e` skill to extract Broadleaf's shipping
estimation behavior into a Node.js/TypeScript service.

## Goal

Build a demo-ready vertical slice that proves shipping estimate extraction with
focused API tests and a runtime smoke check.

## Scope

### In Scope

- Fulfillment and shipping price estimation.
- Behavior discovered from the existing Java providers.
- Test expectations derived from fulfillment pricing tests.
- A minimal service contract that is enough to prove the critical path.

### Out of Scope

- Catalog, cart, checkout, payment, tax, inventory, product, or SKU ownership.
- Broadleaf adapter wiring.
- UI work.
- Platform-complete service design.

## Workflow

1. Summarize the files and directories you expect to touch.
2. Summarize the verification path before editing.
3. Use subagents for:
   - Legacy behavior investigation.
   - API contract design.
   - Test mapping.
4. Implement the smallest service that proves the extraction.
5. Verify the behavior with API tests.
6. Run `service-runtime-smoke` as the final proof step.

## Smoke Test Requirements

`service-runtime-smoke` should:

- Start or verify the service.
- Hit the health endpoint.
- Hit the main shipping estimation endpoint with representative payloads.
- Report status codes and key response fields.

## Stop Condition

Stop when the critical path is proven. Do not implement adapter or UI wiring
unless explicitly requested.
