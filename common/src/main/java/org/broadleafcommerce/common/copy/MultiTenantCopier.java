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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.service.GenericEntityService;
import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.util.StreamCapableTransactionalOperationAdapter;
import org.broadleafcommerce.common.util.StreamingTransactionCapableUtil;
import org.broadleafcommerce.common.util.tenant.IdentityExecutionUtils;
import org.broadleafcommerce.common.util.tenant.IdentityOperation;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.EnforceEnterpriseCollectionBehaviorState;
import org.springframework.core.Ordered;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.Embeddable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 * Abstract class for copying entities to a new catalog as required during derived catalog propagation. Subclasses generally
 * call {@link #copyEntitiesOfType(Class, org.broadleafcommerce.common.site.domain.Site, org.broadleafcommerce.common.site.domain.Catalog, MultiTenantCopyContext)}
 * one or more times inside of their {@link #copyEntities(MultiTenantCopyContext)} implementation to clone and persist
 * an entity object tree.
 * 
 * @author Andre Azzolini (apazzolini)
 * @author Jeff Fischer
 */
public abstract class MultiTenantCopier implements Ordered {
    protected static final Log LOG = LogFactory.getLog(MultiTenantCopier.class);
    
    @Resource(name = "blGenericEntityService")
    protected GenericEntityService genericEntityService;
    
    @Resource(name = "blMultiTenantCopierExtensionManager")
    protected MultiTenantCopierExtensionManager extensionManager;

    @Resource(name="blStreamingTransactionCapableUtil")
    protected StreamingTransactionCapableUtil transUtil;
    
    protected int order = 0;

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

    protected <T, G extends Exception> void persistCopyObjectTree(CopyOperation<T,G> copyOperation, Class<T> clazz, T original, MultiTenantCopyContext context) throws G {
        try {
            //don't persist if there is already an equivalent present
            if (context.getEquivalentId(clazz.getName(), genericEntityService.getIdentifier(original)) != null) {
                return;
            }

            context.clearOriginalIdentifiers();
            genericEntityService.clearAutoFlushMode();
            Object copy = copyOperation.execute(original);
            BroadleafRequestContext.getBroadleafRequestContext().setEnforceEnterpriseCollectionBehaviorState(EnforceEnterpriseCollectionBehaviorState.FALSE);
            persistCopyObjectTreeInternal(copy, new HashSet<Integer>(), context);
            genericEntityService.flush();
        } catch (Exception e) {
            LOG.error("Unable to persist the copy object tree", e);
            throw ExceptionHelper.refineException(e);
        } finally {
            BroadleafRequestContext.getBroadleafRequestContext().setEnforceEnterpriseCollectionBehaviorState(EnforceEnterpriseCollectionBehaviorState.TRUE);
            context.clearOriginalIdentifiers();
            genericEntityService.enableAutoFlushMode();
        }
    }

    protected void persistCopyObjectTreeInternal(Object copy, Set<Integer> library, MultiTenantCopyContext context) {
        if (library.contains(System.identityHashCode(copy))) {
            return;
        }
        library.add(System.identityHashCode(copy));
        List<Object[]> collections = new ArrayList<Object[]>();
        Field[] allFields = context.getAllFields(copy.getClass());
        for (Field field : allFields) {
            if (field.getName().equals("embeddableSiteDiscriminator")) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                Object newTarget;
                try {
                    newTarget = field.get(copy);
                } catch (IllegalAccessException e) {
                    throw ExceptionHelper.refineException(e);
                }
                if (newTarget != null) {
                    if (field.getAnnotation(ManyToOne.class) != null || field.getAnnotation(OneToOne.class) != null) {
                        if (library.contains(System.identityHashCode(newTarget))) {
                            persistNode(newTarget, context);
                            continue;
                        }
                        persistCopyObjectTreeInternal(newTarget, library, context);
                    } else if (field.getAnnotation(ManyToMany.class) != null || field.getAnnotation(OneToMany.class) != null) {
                        collections.add(new Object[]{field, newTarget});
                    } else if (field.getType().getAnnotation(Embeddable.class) != null && MultiTenantCloneable.class.isAssignableFrom(field.getType())) {
                        persistCopyObjectTreeInternal(newTarget, library, context);
                    }
                }
            }
        }
        if (copy.getClass().getAnnotation(Embeddable.class) == null) {
            persistNode(copy, context);
        }
        for (Object[] collectionItem : collections) {
            if (collectionItem[1] instanceof Collection) {
                Collection newCollection = (Collection) collectionItem[1];
                for (Object member : newCollection) {
                    persistCopyObjectTreeInternal(member, library, context);
                }
            } else if (collectionItem[1] instanceof Map) {
                Map newMap = (Map) collectionItem[1];
                for (Object key : newMap.keySet()) {
                    persistCopyObjectTreeInternal(newMap.get(key), library, context);
                }
            } else {
                throw new IllegalArgumentException(String.format("During copy object persistence, " +
                        "an unrecognized type was detected for a OneToMany or ManyToMany field. The system currently only " +
                        "recognizes Collection and Map. (%s.%s)", copy.getClass().getName(), ((Field) collectionItem[0]).getName()));
            }
        }
    }

    protected void persistNode(final Object copy, final MultiTenantCopyContext context) {
        if (!genericEntityService.sessionContains(copy) && !genericEntityService.idAssigned(copy)) {
            final Object original = genericEntityService.readGenericEntity(copy.getClass().getName(), context.removeOriginalIdentifier(copy));
            extensionManager.getProxy().transformCopy(context, original, copy);
            extensionManager.getProxy().prepareForSave(context, original, copy);

            IdentityExecutionUtils.runOperationByIdentifier(new IdentityOperation<Void, RuntimeException>() {
                @Override
                public Void execute() {
                    genericEntityService.persist(copy);
                    extensionManager.getProxy().postSave(context, original, copy);
                    return null;
                }
            }, context.getToSite(), context.getToSite(), context.getToCatalog());

            context.storeEquivalentMapping(original.getClass().getName(), context.getIdentifier(original), context.getIdentifier(copy));
        }
    }

    /**
     * Subclasses will generally call this method in their {@link #copyEntities(MultiTenantCopyContext)} implementation.
     *
     * @param clazz
     * @param fromSite
     * @param fromCatalog
     * @param context
     * @param <T>
     * @throws ServiceException
     * @throws CloneNotSupportedException
     */
    protected <T extends MultiTenantCloneable> void copyEntitiesOfType(final Class<T> clazz, final Site fromSite, final Catalog fromCatalog, final MultiTenantCopyContext context)
            throws ServiceException, CloneNotSupportedException {
        genericEntityService.flush();
        genericEntityService.clear();
        transUtil.runStreamingTransactionalOperation(new StreamCapableTransactionalOperationAdapter() {
            @Override
            public Object[] retrievePage(int startPos, int pageSize) {
                try {
                    Object[] temp = new Object[1];
                    List<T> results = readAll(clazz, pageSize, startPos, fromSite, fromCatalog);
                    temp[0] = results;
                    return temp;
                } catch (ServiceException e) {
                    throw ExceptionHelper.refineException(e);
                }
            }

            @Override
            public void pagedExecute(Object[] param) throws Throwable {
                try {
                    List<T> results = (List<T>) param[0];
                    for (T result : results) {
                        persistCopyObjectTree(new CopyOperation<T, CloneNotSupportedException>() {
                            @Override
                            public T execute(T original) throws CloneNotSupportedException {
                                return (T) original.createOrRetrieveCopyInstance(context).getClone();
                            }
                        }, clazz, result, context);
                    }
                } finally {
                    genericEntityService.clear();
                }
            }

            @Override
            public Long retrieveTotalCount() {
                try {
                    return readCount(clazz, fromSite, fromCatalog);
                } catch (ServiceException e) {
                    throw ExceptionHelper.refineException(e);
                }
            }

            @Override
            public boolean shouldRetryOnTransactionLockAcquisitionFailure() {
                return true;
            }
        }, RuntimeException.class);
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
        }, context.getToSite(), context.getToSite(), context.getToCatalog());
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
        }, site, site, catalog);
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
     * Returns the primary key values for all entities of the specified type in the site or catalog.
     *
     * @param clazz
     * @param site
     * @param catalog
     * @return
     * @throws ServiceException
     */
    protected List<Long> readAllIds(final Class<?> clazz, Site site, Catalog catalog) throws ServiceException {
        return IdentityExecutionUtils.runOperationByIdentifier(new IdentityOperation<List<Long>, ServiceException>() {
            @Override
            public List<Long> execute() throws ServiceException {
                return genericEntityService.readAllGenericEntityId(clazz);
            }
        }, site, site, catalog);
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
        }, site, site, catalog);
    }

}
