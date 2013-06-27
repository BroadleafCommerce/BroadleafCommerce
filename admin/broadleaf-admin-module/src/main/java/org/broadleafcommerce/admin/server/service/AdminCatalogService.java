/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
