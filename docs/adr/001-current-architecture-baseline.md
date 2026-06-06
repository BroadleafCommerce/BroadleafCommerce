# ADR-001: Current Architecture Baseline

## Status

Accepted

## Context

Broadleaf Commerce Community Edition (CE) is an enterprise Java e-commerce framework targeting companies with under $5M in revenue. It is source-available under a Fair Use dual-license with commercial restrictions.

## Decision

Document the current architecture as the baseline for future decisions.

## Architecture Style

**Traditional Unified Monolith** — Single codebase with `site` and `admin` deployment sharing core dependencies. Not microservices-based (Microservices Edition is a separate commercial product).

## Major Technologies

| Component | Technology | Version |
|-----------|------------|---------|
| Language | Java | 17 |
| Framework | Spring |6.2.18 |
| Security | Spring Security | 6.5.10 |
| ORM | Hibernate (JPA) | 5.6.15.Final |
| Database | Any JPA-compatible (RDBMS) | — |
| Search | Apache Solr | 9.9.0 (client) |
| Scheduling | Quartz | 2.5.2 |
| Caching | ehcache3 | 3.10.8 |
| Build | Maven | 3.x |
| Testing | JUnit 4, Spock 2 | — |

## Module Structure

```
broadleaf (root pom — 7.0.8-SNAPSHOT)
├── common/ # Shared utilities
├── core/
│   ├── broadleaf-framework/   # Domain entities, services, workflows
│   ├── broadleaf-framework-web/ # Web layer (MVC, controllers)
│   ├── broadleaf-profile/     # Customer profile domain
│   └── broadleaf-profile-web/ # Profile web
├── admin/
│   ├── broadleaf-admin-module/
│   ├── broadleaf-contentmanagement-module/
│   ├── broadleaf-open-admin-platform/ # Admin UI
│   └── broadleaf-admin-functional-tests/
└── integration/               # Integration tests
```

## Key Tradeoffs

| Decision | Tradeoff |
|----------|----------|
| Java/Spring monolithic | Mature ecosystem, but less cloud-native than modern stacks |
| JPA/Hibernate | Portable but ORM overhead |
| Solr search | Powerful but separate infrastructure |
| Framework vs SaaS | Full control but more operational burden |

## Known Constraints

- Java 17 required
- Servlet container required (Tomcat, Jetty, Undertow)
- Database must be JPA-compatible RDBMS
- Solr server must be provisioned separately
- No built-in metrics/observability
- No database migration system (schema via JPA annotations)

## Known Unknowns

- Exact schema migration strategy (likely manual SQL patches)
- WebSocket usage not confirmed
- Specific CI/CD pipeline details (Jenkinsfile present but not reviewed)
- Python e2e test integration (may be in separate repo)

## Evidence

- [pom.xml](https://github.com/johrenberger/BroadleafCommerce/blob/8645873661b34fe0954cebe382aba59336714db0/pom.xml)
- [README.md](https://github.com/johrenberger/BroadleafCommerce/blob/8645873661b34fe0954cebe382aba59336714db0/README.md)