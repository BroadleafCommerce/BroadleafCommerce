/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.broadleafcommerce.openadmin.server.factory;

import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;

/**
 * Responsible for creating different persistence packages for different operations
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface PersistencePackageFactory {

    /**
     * Creates a persistence package for the given request. Different request types require different combinations
     * of attributes, which are generally self explanatory.
     * 
     * @param request
     * @return the persistence package
     * 
     * @see PersistencePackageRequest
     * @see PersistencePackageRequest.Type
     */
    public PersistencePackage create(PersistencePackageRequest request);

}
