/*
 * Copyright 2012 the original author or authors.
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

package org.broadleafcommerce.admin.client.service;

import org.springframework.security.access.annotation.Secured;

import com.google.gwt.user.client.rpc.RemoteService;
import com.gwtincubator.security.exception.ApplicationSecurityException;

/**
 * 
 * @author Phillip Verheyden
 *
 */
public interface AdminCatalogService extends RemoteService {
    
    /**
     * Clear out any Skus that are already attached to the Product
     * if there were any there and generate a new set of Skus based
     * on the permutations of ProductOptions attached to this Product
     * 
     * @param productId - the Product to generate Skus from
     * @return the number of generated Skus from the ProductOption permutations
     */
    @Secured("PERMISSION_OTHER_DEFAULT")
    public Integer generateSkusFromProduct(Long productId) throws ApplicationSecurityException;

    @Secured("PERMISSION_OTHER_DEFAULT")
    public Boolean cloneProduct(Long productId) throws ApplicationSecurityException;

}
