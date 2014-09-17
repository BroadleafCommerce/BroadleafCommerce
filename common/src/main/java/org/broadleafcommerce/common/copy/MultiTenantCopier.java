/*
 * #%L
 * broadleaf-multitenant-singleschema
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.service.GenericEntityService;
import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.util.tenant.IdentityExecutionUtils;
import org.broadleafcommerce.common.util.tenant.IdentityOperation;
import org.springframework.core.Ordered;

import java.util.List;

import javax.annotation.Resource;

/**
 * Abstract class for copying entities to a new catalog as required during derived catalog propagation.
 * Implementations of this class should take care to:
 * 
 * <ul>
 *   <li>Make sure that all reads are happening in the appropriate context</li>
 *   
 *   <li>Make sure that the clone method is invoked via an implementation of {@link CopyOperation}
 *       and is fed through {@link #executeSmartObjectCopy(MultiTenantCopyContext, Object, CopyOperation)}</li>
 *       
 *   <li></li>
 * </ul>
 * 
 * @author Andre Azzolini (apazzolini)
 */
public abstract class MultiTenantCopier implements Ordered {
    protected static final Log LOG = LogFactory.getLog(MultiTenantCopier.class);
    
    @Resource(name = "blGenericEntityService")
    protected GenericEntityService genericEntityService;
    
    @Resource(name = "blMultiTenantCopierExtensionManager")
    protected MultiTenantCopierExtensionManager extensionManager;
    
    protected int order = 0;
    
    /**
     * Wrapper method that should be used for all cloning operations. This method will take care of mapping
     * original object ids, associating the created object as a clone of the original in the context, and calling
     * a database save on the cloned object.
     * 
     * This method will return null if the given from object already has a corresponding clone in the context map.
     * 
     * @param context
     * @param from
     * @param operation
     * @return the cloned object, or null if it had previously been cloned
     * @throws G
     * @throws ServiceException
     */
    public <T extends Object, G extends Exception> T executeSmartObjectCopy(MultiTenantCopyContext context, T from, 
            CopyOperation<T, G> operation) throws G, ServiceException {
        if (from instanceof Status && 'Y' == ((Status) from).getArchived()) {
            return null;
        }
        
        final Object fromId = operation.getId(from);
        final Object copyId = context.getEquivalentId(operation.getCacheClass().getName(), fromId);
        
        if (copyId != null) {
            return context.getClonedVersion(operation.getCacheClass(), fromId);
        }

        T copy = operation.execute();
        if (copy == null) {
            return null;
        }
        
        extensionManager.getProxy().prepareForSave(context, from, copy);
        
        copy = save(context, copy);

        context.storeEquivalentMapping(operation.getCacheClass().getName(), fromId, operation.getId(copy));
        return copy;
    }
    
    /**
     * Saves the specified object in the toSite and toCatalog of the given context.
     * 
     * @param context
     * @param object
     * @return the saved entity
     * @throws ServiceException
     */
    protected <T> T save(MultiTenantCopyContext context, final T object) throws ServiceException {
        return IdentityExecutionUtils.runOperationByIdentifier(new IdentityOperation<T, ServiceException>() {
            @Override
            public T execute() throws ServiceException {
                return genericEntityService.save(object);
            }
        }, context.getToSite(), context.getToCatalog());
    }
    
    /**
     * Returns the count of the given entity class for the specified site and catalog
     * 
     * @param clazz
     * @param site
     * @param catalog
     * @return the count
     * @throws ServiceException
     */
    protected <T> Long readCount(final Class<T> clazz, Site site, Catalog catalog) throws ServiceException {
        return IdentityExecutionUtils.runOperationByIdentifier(new IdentityOperation<Long, ServiceException>() {
            @Override
            public Long execute() throws ServiceException {
                return genericEntityService.readCountGenericEntity(clazz);
            }
        }, site, catalog);
    }
    
    /**
     * @see #readAll(Class, int, int, Site, Catalog)
     * 
     * @param clazz
     * @param site
     * @param catalog
     * @return the list of entities for the specified parameters
     * @throws ServiceException
     */
    protected <T> List<T> readAll(Class<T> clazz, Site site, Catalog catalog) throws ServiceException {
        return readAll(clazz, Integer.MAX_VALUE, 0, site, catalog);
    }

    /**
     * Returns a list of all entities in the system for the given class, site, and catalog. Additionally,
     * this method supports pagination.
     * 
     * @param clazz
     * @param limit
     * @param offset
     * @param site
     * @param catalog
     * @return the list of entities for the specified parameters
     * @throws ServiceException
     */
    protected <T> List<T> readAll(final Class<T> clazz, final int limit, final int offset, Site site, 
            Catalog catalog) throws ServiceException {
        return IdentityExecutionUtils.runOperationByIdentifier(new IdentityOperation<List<T>, ServiceException>() {
            @Override
            public List<T> execute() throws ServiceException {
                return genericEntityService.readAllGenericEntity(clazz, limit, offset);
            }
        }, site, catalog);
    }


    /**
     * Main method that should be implemented by each {@link MultiTenantCopier} to drive the logic of
     * copying that particular entity to the new derived catalog.
     * 
     * @param context
     */
    public abstract void copyEntities(MultiTenantCopyContext context) throws Exception;

    /**
     * 
     * @return the order of this {@link MultiTenantCopier}
     */
    @Override
    public int getOrder() {
        return order;
    }
    
    public void setOrder(int order) {
        this.order = order;
    }

}
