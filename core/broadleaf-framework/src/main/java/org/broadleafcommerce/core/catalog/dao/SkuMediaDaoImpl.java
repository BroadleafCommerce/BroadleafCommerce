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

import org.broadleafcommerce.common.util.dao.TypedQueryBuilder;
import org.broadleafcommerce.core.catalog.domain.SkuMediaXref;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * {@inheritDoc}
 *
 * @author Chris Kittrell (ckittrell)
 */
@Repository("blSkuMediaDao")
public class SkuMediaDaoImpl implements SkuMediaDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Override
    public SkuMediaXref save(SkuMediaXref skuMediaXref) {
        return em.merge(skuMediaXref);
    }

    @Override
    public List<SkuMediaXref> readSkuMediaBySkuId(Long skuId) {
        TypedQuery<SkuMediaXref> query = new TypedQueryBuilder<>(SkuMediaXref.class, "xref")
                .addRestriction("xref.sku.id", "=", skuId)
                .toQuery(em);

        return query.getResultList();
    }

}
