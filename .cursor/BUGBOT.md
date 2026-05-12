# Bugbot Extracted-Service Review

When reviewing PRs that add or change an extracted service, prioritize findings that can change production behavior, pricing, checkout totals, security posture, or merge-time verification.

For legacy-to-service extractions:

- Compare service defaults against legacy defaults when an optional request field affects money, validation, eligibility, routing, security, or persisted behavior. Flag missing explicit defaults at the service boundary.
- Check that validation and calculation agree. If validation accepts or exempts a request because a field exists, confirm the calculation path uses the same condition and cannot silently ignore that field.
- Verify legacy-derived behavior is covered at the service API boundary, not only in private helper tests. Look for tests covering happy paths, edge cases, invalid inputs, default behavior, and legacy configuration flags.
- When a PR adds a new deployable service, verify it is wired into repo-level CI/build/test execution or that the PR explicitly marks it as a non-deployed prototype. Flag services with their own package manifest, test command, build command, or lockfile that can merge without those commands running.
- If the service is meant to replace or back a legacy path, verify there is either adapter/integration wiring or a clear statement that wiring is out of scope. Do not treat an unwired service as production-ready.
- Prefer findings with concrete blast radius: wrong customer totals, merchant under/overcharging, checkout regressions, security leakage, or untested deployable code.
- Do not require broad adapter/UI integration unless the PR claims the legacy app now uses the service.
