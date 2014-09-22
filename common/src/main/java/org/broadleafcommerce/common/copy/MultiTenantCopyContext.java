/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.common.copy;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.service.GenericEntityService;
import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.util.tenant.IdentityExecutionUtils;
import org.broadleafcommerce.common.util.tenant.IdentityOperation;

import java.util.HashMap;
import java.util.Map;

public class MultiTenantCopyContext {
    
    protected Catalog fromCatalog;
    protected Catalog toCatalog;
    protected Site fromSite;
    protected Site toSite;
    
    protected Map<String, Map<Object, Object>> equivalentsMap;
    protected GenericEntityService genericEntityService;
    
    public MultiTenantCopyContext(Catalog fromCatalog, Catalog toCatalog, Site fromSite, Site toSite, 
            GenericEntityService genericEntityService) {
        equivalentsMap = new HashMap<String, Map<Object, Object>>();
        this.fromCatalog = fromCatalog;
        this.toCatalog = toCatalog;
        this.fromSite = fromSite;
        this.toSite = toSite;
        this.genericEntityService = genericEntityService;
    }

    public <T> T getClonedVersion(final Class<T> clazz, final Object originalId) throws ServiceException {
        return IdentityExecutionUtils.runOperationByIdentifier(new IdentityOperation<T, ServiceException>() {
            @Override
            @SuppressWarnings("unchecked")
            public T execute() throws ServiceException {
                Object cloneId = getEquivalentId(clazz.getName(), originalId);

                if (cloneId == null) {
                    return null;
                }

                return (T) genericEntityService.readGenericEntity(clazz.getName(), cloneId);
            }
        }, getToSite(), getToCatalog());
    }

    
    public Object getEquivalentId(String className, Object fromId) {
        Map<Object, Object> keys = equivalentsMap.get(className);
        return keys == null ? null : keys.get(fromId);
    }

    public void storeEquivalentMapping(String className, Object fromId, Object toId) {
        Map<Object, Object> keys = equivalentsMap.get(className);
        if (keys == null) {
            keys = new HashMap<Object, Object>();
            equivalentsMap.put(className, keys);
        }
        
        if (keys.containsKey(fromId)) {
            throw new IllegalArgumentException("Object [" + className + ":" + fromId + "] has already been cloned.");
        }
        
        keys.put(fromId, toId);
    }

    public Catalog getFromCatalog() {
        return fromCatalog;
    }

    public Catalog getToCatalog() {
        return toCatalog;
    }

    public Site getFromSite() {
        return fromSite;
    }

    public Site getToSite() {
        return toSite;
    }

}
