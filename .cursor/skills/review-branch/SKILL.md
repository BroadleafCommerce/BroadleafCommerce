---
name: review-branch
description: Review the current branch or diff for correctness, regressions, security, missing tests, and merge readiness. Use when the user asks for a branch review, PR review, or pre-merge check.
---

# Review Branch

## Review Stance

Prioritize issues that can break behavior, create security risk, weaken maintainability, or leave important intent untested. Do not spend review budget on taste unless it hides a real risk.

## Workflow

1. Inspect git status, unstaged diff, staged diff, and commits since the base branch.
2. Identify the intended behavior from code, tests, commit messages, and user context.
3. Review changed files and nearby call sites touched by the behavior.
4. Check whether tests prove the intended behavior and the likely regression paths.
5. Run targeted verification when possible.
6. Lead with findings ordered by severity. Include file references and concrete failure modes.

## Output Format

```markdown
## Findings
- [Severity] `path`: [issue, impact, suggested fix]

## Open Questions
- [Question or "None"]

## Verification
- Ran: `[command]`
- Not run: [reason]
```

If no issues are found, say that clearly and still mention test gaps or residual risk.
