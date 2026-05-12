# Shipping Estimator Extraction Prompt

Use the `legacy-java-to-ts-service-e2e` skill to extract Broadleaf's shipping
estimation behavior into a small Node.js/TypeScript service.

Goal: build the smallest demo-ready vertical slice that preserves the legacy
shipping estimate behavior and proves it with API tests plus a runtime smoke
test.

Scope only shipping estimate calculation and validation. Treat the existing Java
providers and fulfillment pricing tests as the source of truth. Do not move
catalog, cart, checkout, payment, tax, inventory, product, SKU, adapter, or UI
ownership into the service.

Work fast but verify:

1. Use subagents in parallel for legacy behavior, API contract, and test mapping.
2. Before editing, summarize the files you expect to touch and the verification plan.
3. Implement the simplest typed API and service code that covers the discovered critical path.
4. Add focused API tests for one happy path, one validation failure, and one legacy edge case.
5. Run the tests and fix only relevant failures.
6. Run `service-runtime-smoke` against the health endpoint and main shipping endpoint.

Quality bar: keep the service small, typed, deterministic for money/rounding
behavior, and explicit about validation errors.

Stop once tests and smoke prove the critical path. Do not implement adapter or
UI wiring unless explicitly requested.
