/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2026 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 *
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.catalog.domain;

import junit.framework.TestCase;

public class SkuImplTest extends TestCase {

    public void testEqualsComparesIdsOfProxiedSkus() {
        SkuImpl sku = createSku(1L, "Tabasco");
        SkuImpl proxiedSameName = new ProxyLikeSku(2L, "Tabasco");
        SkuImpl proxiedSameId = new ProxyLikeSku(1L, "Tabasco");

        assertFalse(sku.equals(proxiedSameName));
        assertTrue(sku.equals(proxiedSameId));
    }

    public void testEqualsFallsBackToNameWithoutIds() {
        assertTrue(createSku(null, "Tabasco").equals(createSku(null, "Tabasco")));
        assertFalse(createSku(null, "Tabasco").equals(createSku(null, "Sriracha")));
    }

    protected SkuImpl createSku(Long id, String name) {
        SkuImpl sku = new SkuImpl();
        sku.setId(id);
        sku.setName(name);
        return sku;
    }

    /**
     * Mimics an uninitialized Hibernate proxy: the id field is never populated, but getId() returns the identifier.
     */
    protected static class ProxyLikeSku extends SkuImpl {

        private final Long proxyId;

        ProxyLikeSku(Long proxyId, String name) {
            this.proxyId = proxyId;
            setName(name);
        }

        @Override
        public Long getId() {
            return proxyId;
        }
    }

}
