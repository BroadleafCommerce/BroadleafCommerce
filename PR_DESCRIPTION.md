## Description

**Fixes:** Performance degradation and deep-pagination overhead during CMS Page SiteMap generation.

### Background & Problem
When generating sitemap entries for CMS pages, the system previously used offset-based pagination (`setFirstResult/setMaxResults`) in a loop, fetching full `PageImpl` entities. As the total page count grew, this implementation introduced significant performance risks:
1. **Deep-pagination penalty**: Database scan costs rose superlinearly with each offset window.
2. **ORM Hydration Overhead**: Full `PageImpl` entities were hydrated per row, which sequentially triggered eager fetching of the `@ManyToOne` relationship to `PageTemplate`—even though only the ID, Full URL, and Last Updated Date were actually required.
3. **Query Cache Churn**: Caching the offset queries generated very low hit rates but occupied cache space.

### Solution / Changes
This PR redesigns the sitemap fetch path to utilize a **keyset/seek pagination** loop combined with **lightweight DTO projection**:

* **Introduced `SiteMapPageDTO`**: A lightweight data transfer object mapped strictly to `id`, `fullUrl`, and `dateUpdated`.
* **New Keyset Query in `PageDao`**: Added `readOnlineAndIncludedPageSiteMapEntries(int limit, String lastFullUrl)`. This query iterates through the default `FULL_URL` index ensuring rapid cursor-based traversal without massive offset scans.
* **Optimized Generator Logic**: Updated `PageSiteMapGenerator.java` to drive the loop using the last seen URL (`lastFullUrl`) as the seek boundary. 
* **Cache Management**: The new keyset query has its Hibernate `QueryHints.HINT_CACHEABLE` toggle turned off locally to explicitly prevent cache-churn over dynamic keysets boundaries.
* **Test Maintenance**: Updated `PageSiteMapGeneratorTest` mock structures to utilize the new DTO response payload.

### Acceptance Criteria Passed
- [x] Sitemap output behavior strictly remains equal (including automatic duplicate-url skipping mechanics internally utilized by the generator).
- [x] Unnecessary ORM bindings (like `PageTemplate`) are safely bypassed.
- [x] `mvn clean test` succeeds locally.
