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

import org.broadleafcommerce.core.catalog.domain.SkuMediaXref;

import java.util.List;

/**
 * {@link SkuMediaDao} provides persistence access to {@link SkuMediaXref} instances
 *
 * @author Chris Kittrell (ckittrell)
 */
public interface SkuMediaDao {

    /**
     * Persist a {@link SkuMediaXref} instance to the datastore
     *
     * @param skuMediaXref the skuMediaXref to persist
     * @return the saved state of the passed in skuMediaXref
     */
    SkuMediaXref save(SkuMediaXref skuMediaXref);

    /**
     * Retrieve a list of {@link SkuMediaXref} instances by its sku id
     *
     * @param skuId the sku id of the skuMediaXref
     * @return the skuMediaXrefs with this sku id
     */
    List<SkuMediaXref> readSkuMediaBySkuId(Long skuId);

}
