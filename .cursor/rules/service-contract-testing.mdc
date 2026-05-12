---
description: Contract testing standards for extracted TypeScript services
globs: "**/*.{ts,tsx,js,jsx}"
alwaysApply: false
---

# Service Contract Testing

For extracted services:

- Derive API tests from legacy behavior and name the legacy case being protected.
- Test business outcomes at the service boundary, not private helper details.
- Cover happy paths, edge cases, invalid inputs, and boundary ownership errors.
- Include explainability or audit responses when the endpoint returns business decisions.
- Run service API tests before browser smoke tests.
- Never report parity unless the legacy-derived cases were actually executed.

For the shipping-estimator pilot, include fixed-rate, price-band, weight-band, flat-rate item adjustments, invalid band configuration, and explain responses.
