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

import org.hibernate.annotations.Cascade;
import org.junit.Test;

import javax.persistence.CascadeType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * An owned one-to-one (one that cascades removal) is part of its parent for section crumb purposes (QA#5512).
 */
public class AdminSecurityServiceRemoteOwnedOneToOneTest {

    private final AdminSecurityServiceRemote service = new AdminSecurityServiceRemote();

    @SuppressWarnings("unused")
    private static class Sample {
        // like Product.defaultSku
        @OneToOne(cascade = {CascadeType.ALL})
        @Cascade(value = {org.hibernate.annotations.CascadeType.ALL})
        Object jpaAndHibernateAll;

        @OneToOne(cascade = CascadeType.REMOVE)
        Object jpaRemove;

        @OneToOne(orphanRemoval = true)
        Object orphanRemoval;

        @OneToOne
        @Cascade(org.hibernate.annotations.CascadeType.DELETE)
        Object hibernateDelete;

        // like Sku.defaultProduct: points at an independently managed entity
        @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
        Object persistMergeRefresh;

        @OneToOne
        Object plain;

        @ManyToOne(cascade = CascadeType.ALL)
        Object manyToOne;
    }

    @Test
    public void cascadingRemovalIsOwned() throws Exception {
        assertTrue(owned("jpaAndHibernateAll"));
        assertTrue(owned("jpaRemove"));
        assertTrue(owned("orphanRemoval"));
        assertTrue(owned("hibernateDelete"));
    }

    @Test
    public void referenceWithoutRemovalIsNotOwned() throws Exception {
        assertFalse(owned("persistMergeRefresh"));
        assertFalse(owned("plain"));
        assertFalse(owned("manyToOne"));
    }

    private boolean owned(String field) throws NoSuchFieldException {
        return service.isOwnedOneToOne(Sample.class.getDeclaredField(field));
    }

}
