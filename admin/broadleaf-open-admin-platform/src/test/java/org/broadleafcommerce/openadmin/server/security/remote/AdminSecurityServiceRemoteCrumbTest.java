/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.security.remote;

import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.openadmin.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.dto.SectionCrumb;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Section crumbs are client-supplied; they may only authorize a target that is reachable from them (QA#5506).
 */
public class AdminSecurityServiceRemoteCrumbTest {

    private static final String PRODUCT = "test.Product";
    private static final String SKU = "test.Sku";
    private static final String SKU_MEDIA = "test.SkuMediaXref";
    private static final String CATEGORY = "test.Category";
    private static final String CUSTOMER = "test.Customer";
    private static final String ADMIN_ROLE = "test.AdminRole";

    private static final List<String> NONE = Collections.emptyList();

    private final AdminSecurityServiceRemote service = new AdminSecurityServiceRemote() {
        final Map<String, Set<CrumbRelationship>> fixture = new HashMap<String, Set<CrumbRelationship>>();

        {
            fixture.put(PRODUCT + "->" + SKU, EnumSet.of(CrumbRelationship.COLLECTION, CrumbRelationship.TO_ONE));
            fixture.put(SKU + "->" + SKU_MEDIA, EnumSet.of(CrumbRelationship.COLLECTION));
            fixture.put(PRODUCT + "->" + CATEGORY, EnumSet.of(CrumbRelationship.TO_ONE, CrumbRelationship.JOIN_ENTITY));
        }

        @Override
        protected Set<CrumbRelationship> getCrumbRelationships(String parent, String child) {
            Set<CrumbRelationship> relationships = fixture.get(parent + "->" + child);
            return relationships == null ? EnumSet.noneOf(CrumbRelationship.class) : relationships;
        }
    };

    @Test
    public void unrelatedCrumbNeverAuthorizesTarget() {
        // a crumb for an entity unrelated to the target must not broaden the authorization ceiling
        assertEquals(NONE, crumbs(ADMIN_ROLE, EntityOperationType.FETCH, false, CUSTOMER));
        assertEquals(NONE, crumbs(ADMIN_ROLE, EntityOperationType.ADD, false, CUSTOMER));
        // appending the target itself doesn't make the unrelated crumb in front of it count
        assertEquals(list(ADMIN_ROLE), crumbs(ADMIN_ROLE, EntityOperationType.ADD, false, CUSTOMER, ADMIN_ROLE));
    }

    @Test
    public void toOneLookupAuthorizesReadOnly() {
        // a foreign-key lookup from the Product (Product.defaultCategory)
        assertEquals(list(PRODUCT), crumbs(CATEGORY, EntityOperationType.FETCH, false, PRODUCT));
        assertEquals(list(PRODUCT), crumbs(CATEGORY, EntityOperationType.INSPECT, false, PRODUCT));
        // ... but never a write on the looked-up entity
        assertEquals(NONE, crumbs(CATEGORY, EntityOperationType.UPDATE, false, PRODUCT));
    }

    @Test
    public void ownCollectionAuthorizesWrites() {
        // editing a member of the Product's own Sku collection
        assertEquals(list(PRODUCT), crumbs(SKU, EntityOperationType.UPDATE, false, PRODUCT));
        assertEquals(list(PRODUCT), crumbs(SKU, EntityOperationType.REMOVE, false, PRODUCT));
    }

    @Test
    public void adornedTargetCollectionNeedsServerSignal() {
        // Product.allParentCategoryXrefs add: ceiling is Category behind the xref
        assertEquals(list(PRODUCT), crumbs(CATEGORY, EntityOperationType.ADD, true, PRODUCT));
        // the same crumb on a plain top-level Category update is not an adorned request
        assertEquals(NONE, crumbs(CATEGORY, EntityOperationType.ADD, false, PRODUCT));
        // nor may it reach Category's own adorned collections (crumb for the owning Category section appended)
        assertEquals(list(CATEGORY), crumbs(CATEGORY, EntityOperationType.ADD, true, PRODUCT, CATEGORY));
    }

    @Test
    public void nestedChainIsFollowedOnlyThroughCollections() {
        // Sku media edited from the Sku modal opened from the Product
        assertEquals(list(SKU, PRODUCT), crumbs(SKU_MEDIA, EntityOperationType.UPDATE, false, PRODUCT, SKU));
        // server-appended crumb for the owning section itself (crumb == target)
        assertEquals(list(SKU, PRODUCT), crumbs(SKU, EntityOperationType.UPDATE, false, PRODUCT, SKU));
        // a break anywhere in the chain drops everything in front of it
        assertEquals(list(SKU), crumbs(SKU_MEDIA, EntityOperationType.UPDATE, false, CUSTOMER, SKU));
        assertEquals(list(SKU), crumbs(SKU_MEDIA, EntityOperationType.UPDATE, false, PRODUCT, CUSTOMER, SKU));
    }

    private static List<String> list(String... values) {
        return Arrays.asList(values);
    }

    private List<String> crumbs(String target, EntityOperationType op, boolean adorned, String... crumbClasses) {
        PersistencePackage pp = new PersistencePackage();
        pp.setCeilingEntityFullyQualifiedClassname(target);
        PersistencePerspective perspective = new PersistencePerspective();
        if (adorned) {
            perspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.ADORNEDTARGETLIST,
                    new AdornedTargetList());
        }
        pp.setPersistencePerspective(perspective);
        SectionCrumb[] sectionCrumbs = new SectionCrumb[crumbClasses.length];
        for (int i = 0; i < crumbClasses.length; i++) {
            SectionCrumb crumb = new SectionCrumb();
            crumb.setSectionIdentifier(crumbClasses[i]);
            crumb.setSectionId("1");
            sectionCrumbs[i] = crumb;
        }
        pp.setSectionCrumbs(sectionCrumbs);
        return service.getRelatedSectionCrumbCeilings(pp, op);
    }

}
