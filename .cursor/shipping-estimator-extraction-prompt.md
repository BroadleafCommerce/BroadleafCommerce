# Shipping Estimator Pilot Prompt

Use the `legacy-java-to-ts-service-e2e` skill to extract the Broadleaf shipping estimation logic into a Node.js/TypeScript service.

## Scope

- Focus only on fulfillment/shipping price estimation.
- Treat the existing Java providers and fulfillment pricing tests as the source of truth.
- Do not move catalog, cart, checkout, payment, tax, inventory, product, or SKU ownership into the new service.
- Treat adapter wiring as future work unless I explicitly ask for it.

## Workflow

- Use subagents for legacy behavior, API contract, and test mapping.
- Optimize for a fast demo-ready vertical slice, not a complete platform.
- Implement the smallest service that proves the extraction with API tests.
- After API tests pass, run `service-runtime-smoke` as the final proof step.
- `service-runtime-smoke` should start or verify the service, hit the health endpoint and main shipping endpoints with representative payloads, and report status codes plus key response fields.
- Stop when the critical path is proven; do not implement adapter or UI wiring.

Before editing, summarize the files/directories you expect to touch and the verification path.
