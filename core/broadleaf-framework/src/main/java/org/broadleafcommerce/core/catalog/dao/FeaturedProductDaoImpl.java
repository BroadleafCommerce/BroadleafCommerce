/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.core.catalog.dao;

import org.hibernate.jpa.QueryHints;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository("blFeaturedProductDao")
public class FeaturedProductDaoImpl implements FeaturedProductDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Override
    public boolean isFeaturedProduct(long productId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(p) FROM FeaturedProductImpl p WHERE p.product.id=:pId",
                Long.class
        );
        query.setParameter("pId", productId);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        Long result = query.getSingleResult();
        return result != null && result > 0;
    }

}
