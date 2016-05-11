/*
 * #%L
 * BroadleafCommerce Admin Module
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.admin.server.service;

/**
 * 
 * @author Phillip Verheyden
 *
 */
public interface AdminCatalogService {
    
    /**
     * Clear out any Skus that are already attached to the Product
     * if there were any there and generate a new set of Skus based
     * on the permutations of ProductOptions attached to this Product
     * 
     * @param productId - the Product to generate Skus from
     * @return the number of generated Skus from the ProductOption permutations
     */
    public Integer generateSkusFromProduct(Long productId);

    /**
     * This will create a new product along with a new Sku for the defaultSku, along with new
     * Skus for all of the additional Skus. This is achieved by simply detaching the entities
     * from the persistent session, resetting the primary keys and then saving the entity.
     * 
     * Note: Media for the product is not saved separately, meaning if you make a change to the
     * original product's media items (the one specified by <b>productId</b>) it will change the
     * cloned product's media and vice-versa.
     * 
     * @param productId
     * @return
     */
    public Boolean cloneProduct(Long productId);

}
