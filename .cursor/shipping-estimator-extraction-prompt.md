# Shipping Estimator Extraction Prompt

Use the `legacy-java-to-ts-service-e2e` skill to extract Broadleaf's shipping
estimation behavior into a small Node.js/TypeScript service.

Keep the scope narrow: shipping estimate calculation and validation only. Treat
the existing Java providers and fulfillment pricing tests as the source of
truth. Do not move catalog, cart, checkout, payment, tax, inventory, product, or
SKU ownership into the service.

Before editing, summarize the files you expect to touch and how you will verify
the work. Use subagents for legacy behavior, API contract, and test mapping.
Build the smallest demo-ready slice with API tests, then run
`service-runtime-smoke` against the health endpoint and main shipping endpoint.

Stop once the critical path is proven. Do not implement adapter or UI wiring
unless explicitly requested.
