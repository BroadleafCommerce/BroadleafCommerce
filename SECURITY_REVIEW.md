# Security Review Lab

This repository integrates four complementary security/code-review tools to support
pull request analysis for BroadleafCommerce 7.0.x. Everything described here is
**report-only**: no scan currently fails a build, blocks a merge, or gates a PR. The
goal of this lab is to produce reports for humans to review while we tune each tool's
signal-to-noise ratio before turning any of it into a hard gate.

| Tool | Type | What it detects | Scope |
|---|---|---|---|
| [Semgrep](https://semgrep.dev/) | Pattern-based static analysis (SAST) | Language-level bug patterns, insecure API usage, injection sinks, hardcoded secrets, etc. via the `p/java` and `p/security-audit` rulesets | Source files (`.java`, config, etc.), reactor-wide |
| [SpotBugs](https://spotbugs.github.io/) | Bytecode static analysis | General code-quality/correctness bugs (null derefs, resource leaks, bad equals/hashCode, concurrency bugs, etc.) | Compiled `.class` files, per Maven module |
| [Find Security Bugs](https://find-sec-bugs.github.io/) | SpotBugs detector plugin | Security-specific bug patterns on top of SpotBugs' engine (SQL/LDAP/XPath injection, weak crypto, SSRF, XXE, insecure cookie flags, path traversal, etc.) | Same bytecode SpotBugs already loaded, per Maven module |
| [OWASP Dependency-Check](https://owasp.org/www-project-dependency-check/) | Software Composition Analysis (SCA) | Known published vulnerabilities (CVEs) in third-party dependencies (transitive included) | Whole Maven reactor, single aggregate scan |

## How the tools differ and overlap

- **Semgrep vs. SpotBugs/FindSecBugs**: Semgrep works on *source code* using AST-aware
  pattern matching and can flag issues before code even compiles (including in
  non-Java files). SpotBugs/FindSecBugs work on *compiled bytecode*, which lets them
  catch bugs that are hard to see in source (e.g. certain data-flow/taint issues,
  JVM-level bugs) but requires a successful `mvn compile` first. There is deliberate
  overlap on classic vulnerability classes (e.g. SQL injection, hardcoded credentials)
  by design — treat overlapping findings from both tools as corroborating signal.
- **SpotBugs vs. FindSecBugs**: FindSecBugs is not a separate scanner — it is a
  detector plugin loaded *into* SpotBugs (see the `security-check` Maven profile
  below). SpotBugs' own built-in detectors focus on general correctness/quality bugs;
  FindSecBugs adds ~150 additional security-specific detectors on top of the same
  engine and the same bytecode analysis pass.
- **Dependency-Check vs. the other three**: Dependency-Check does not look at code you
  wrote at all — it identifies known-vulnerable *dependencies* (CVEs) by matching
  library coordinates/hashes against the NVD. Semgrep/SpotBugs/FindSecBugs, in
  contrast, only look at code within this repository. A clean Dependency-Check report
  says nothing about custom code vulnerabilities, and vice versa.

## Where each tool is wired up

### Maven: SpotBugs + FindSecBugs + OWASP Dependency-Check

All three Maven-based tools live in the `security-check` Maven profile in the root
[`pom.xml`](pom.xml), so they never run as part of a normal `mvn install`/`mvn verify`.

- **SpotBugs + FindSecBugs** (`com.github.spotbugs:spotbugs-maven-plugin`, with
  `com.h3xstream.findsecbugs:findsecbugs-plugin` configured as its `<plugins>`
  detector) is bound to the `verify` phase of every module and uses the
  `spotbugs:spotbugs` report-generation goal (not `spotbugs:check`), so it **never
  fails the build** regardless of findings. It runs once per module (the normal way
  SpotBugs is used in a multi-module reactor, since it needs each module's own
  compiled classes) and writes HTML, XML, and SARIF reports to
  `<module>/target/spotbugs/`.
- **OWASP Dependency-Check** (`org.owasp:dependency-check-maven`) is bound to the
  `aggregate` goal with `<inherited>false</inherited>`, so it runs **exactly once**,
  from the reactor root, and analyzes every module's dependencies together instead of
  once per module. It writes HTML, XML, JSON, and SARIF reports to
  `reports/dependency-check/` at the repository root. `failBuildOnCVSS` is set to `11`
  (above the maximum possible CVSS score of 10) to make the report-only intent
  explicit even if a future default changes.

### GitHub Actions: Semgrep

Semgrep isn't a Maven plugin, so it runs as its own job in
[`.github/workflows/security-review.yml`](.github/workflows/security-review.yml)
using the official `semgrep/semgrep` container image, scanning with the `p/java` and
`p/security-audit` registry rulesets. [`.semgrepignore`](.semgrepignore) excludes
build output, generated reports, IDE metadata, and vendored/minified frontend assets
so scans focus on first-party source.

## Running the scans locally

### SpotBugs + Find Security Bugs

```bash
# Compiles all modules, then runs SpotBugs (with FindSecBugs) per module.
mvn verify -Psecurity-check -DskipTests -Ddependency-check.skip=true

# Reports per module, e.g.:
#   core/broadleaf-framework/target/spotbugs/spotbugsXml.xml
#   core/broadleaf-framework/target/spotbugs/spotbugs.html
#   core/broadleaf-framework/target/spotbugs/spotbugsSarif.json
```

### OWASP Dependency-Check

```bash
# Single aggregate scan across the whole reactor (runs from the repo root only).
mvn verify -Psecurity-check -DskipTests -Dspotbugs.skip=true

# Reports:
#   reports/dependency-check/dependency-check-report.html
#   reports/dependency-check/dependency-check-report.xml
#   reports/dependency-check/dependency-check-report.json
#   reports/dependency-check/dependency-check-report.sarif
```

> The first run downloads the NVD CVE data feed and can take a while. Setting an
> `NVD_API_KEY` environment variable (free, from [nvd.nist.gov](https://nvd.nist.gov/developers/request-an-api-key))
> significantly speeds this up and avoids rate limiting; without one, Dependency-Check
> falls back to slower unauthenticated requests.

### Both Maven tools together

```bash
mvn verify -Psecurity-check -DskipTests
```

### Semgrep

```bash
# Requires Semgrep installed locally (pip install semgrep, or use the container image).
semgrep scan --config p/java --config p/security-audit
```

## PR workflow (`.github/workflows/security-review.yml`)

The workflow runs on every pull request (any base branch) and on pushes to
`develop-7.0.x` / `security-review-lab`, with four independent jobs:

1. **`maven-build`** – Sanity-checks that the reactor still compiles (`mvn verify -DskipTests`, no security profile).
2. **`spotbugs`** – Runs `mvn verify -Psecurity-check -DskipTests -Ddependency-check.skip=true`, collects each module's SpotBugs/FindSecBugs reports into `reports/spotbugs/`, and uploads them as the `spotbugs-findsecbugs-reports` artifact.
3. **`dependency-check`** – Runs `mvn verify -Psecurity-check -DskipTests -Dspotbugs.skip=true`, and uploads `reports/dependency-check/` as the `dependency-check-reports` artifact.
4. **`semgrep`** – Runs Semgrep with the `p/java` and `p/security-audit` rulesets, and uploads `reports/semgrep/` as the `semgrep-reports` artifact.

Every job:

- Uses `continue-on-error: true` (at the job or step level) so a scanner finding
  issues — or even erroring out — never blocks the PR or fails required checks.
- Uploads its generated reports as a workflow artifact, downloadable from the
  Actions run summary.
- Uploads its SARIF output to GitHub Code Scanning via
  `github/codeql-action/upload-sarif` on a **best-effort** basis (`continue-on-error:
  true`), so findings can optionally surface in the "Security" tab / PR annotations
  without depending on it for the job (or the workflow) to succeed. This requires
  GitHub Code Scanning to be enabled for the repository; if it isn't, this step simply
  fails harmlessly and the artifact upload still succeeds.

## Explicitly out of scope (for now)

Per the current phase of this lab, the following are intentionally **not** part of
this setup yet: findings baselines/suppressions, dependency version pinning for the
scanning tools themselves, build/PR gating on findings, result caching beyond the NVD
data feed cache, and any other production-hardening steps. These will be layered in
once the initial signal-to-noise ratio has been evaluated.
