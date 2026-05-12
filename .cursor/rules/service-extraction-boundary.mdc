---
description: Boundary discipline for strangler-style service extractions
alwaysApply: true
---

# Service Extraction Boundary

When extracting legacy behavior into a new service:

- Name the capability being extracted before designing code.
- Define what the new service owns, what Broadleaf still owns, and what the adapter is allowed to translate.
- Keep catalog, product/SKU creation, cart persistence, checkout, payment, tax, and inventory ownership in Broadleaf unless the user explicitly scopes that capability.
- Prefer a thin adapter/facade over changing legacy callers broadly.
- Prove behavior at the service API boundary before relying on UI smoke tests.
- Do not add compatibility shims for unimplemented future extractions.

For the shipping-estimator pilot, the TypeScript service owns shipping estimate calculation and validation only.
