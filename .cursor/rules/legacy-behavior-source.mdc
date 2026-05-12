---
description: Identify legacy Java source-of-truth behavior before extraction
globs: "{core,admin,common,integration}/**/*.{java,groovy}"
alwaysApply: false
---

# Legacy Behavior Source

Before extracting a Java capability:

- Read the source class, immediate collaborators, and nearby tests before proposing an endpoint contract.
- Treat existing tests as behavior examples, not just implementation checks.
- Record edge cases, error behavior, default behavior, and extension points.
- Prefer existing Broadleaf seams such as providers, services, workflows, and adapters.
- If source and tests disagree, call out the conflict and prefer the more direct behavior source.
- Do not infer product ownership from a downstream service dependency.

For the shipping-estimator pilot, start with the fulfillment pricing providers and fulfillment pricing tests.
