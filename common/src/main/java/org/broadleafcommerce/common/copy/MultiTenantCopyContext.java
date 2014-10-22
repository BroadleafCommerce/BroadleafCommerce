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

import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.service.GenericEntityService;
import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.util.tenant.IdentityExecutionUtils;
import org.broadleafcommerce.common.util.tenant.IdentityOperation;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MultiTenantCopyContext {

    public static final String[] BROADLEAF_PACKAGE_PREFIXES = {"org.broadleafcommerce","com.broadleafcommerce"};

    protected Catalog fromCatalog;
    protected Catalog toCatalog;
    protected Site fromSite;
    protected Site toSite;
    protected MultiTenantCopierExtensionManager extensionManager;
    protected int count = 1;
    protected Map<Object, Serializable> currentEquivalentMap = new HashMap<Object, Serializable>();
    
    protected Map<String, Map<Object, Object>> equivalentsMap;
    protected GenericEntityService genericEntityService;
    
    public MultiTenantCopyContext(Catalog fromCatalog, Catalog toCatalog, Site fromSite, Site toSite, 
            GenericEntityService genericEntityService, MultiTenantCopierExtensionManager extensionManager) {
        equivalentsMap = new HashMap<String, Map<Object, Object>>();
        this.fromCatalog = fromCatalog;
        this.toCatalog = toCatalog;
        this.fromSite = fromSite;
        this.toSite = toSite;
        this.genericEntityService = genericEntityService;
        this.extensionManager = extensionManager;
    }

    public <T> T getClonedVersion(final Class<T> clazz, final Object originalId) {
        return IdentityExecutionUtils.runOperationByIdentifier(new IdentityOperation<T, RuntimeException>() {
            @Override
            @SuppressWarnings("unchecked")
            public T execute() {
                Object cloneId = getEquivalentId(clazz.getName(), originalId);

                if (cloneId == null) {
                    return null;
                }

                return (T) genericEntityService.readGenericEntity(clazz.getName(), cloneId);
            }
        }, getToSite(), getToSite(), getToCatalog());
    }

    
    public Object getEquivalentId(String className, Object fromId) {
        String ceilingImpl = genericEntityService.getCeilingImplClass(className).getName();
        Map<Object, Object> keys = equivalentsMap.get(ceilingImpl);
        return keys == null ? null : keys.get(fromId);
    }

    public void storeEquivalentMapping(String className, Object fromId, Object toId) {
        //TODO remove this ceiling entity detection when all the copy code is migrated to the new format
        String ceilingImpl = genericEntityService.getCeilingImplClass(className).getName();
        Map<Object, Object> keys = equivalentsMap.get(ceilingImpl);
        if (keys == null) {
            keys = new HashMap<Object, Object>();
            equivalentsMap.put(ceilingImpl, keys);
        }
        
        if (keys.containsKey(fromId)) {
            throw new IllegalArgumentException("Object [" + className + ":" + fromId + "] has already been cloned.");
        }
        
        keys.put(fromId, toId);
    }

//    public <T> T conditionallySaveClone(Object from, Object copy, Class<T> clazz, boolean shouldSave) {
//        if (shouldSave) {
//            extensionManager.getProxy().transformCopy(this, from, copy);
//            extensionManager.getProxy().prepareForSave(this, from, copy);
//            copy = save(copy);
//            storeEquivalentMapping(from.getClass().getName(), getIdentifier(from), getIdentifier(copy));
//            if (count % 20 == 0) {
//                //Make sure level 1 cache does not get out of hand for memory
//                genericEntityService.flush();
//                genericEntityService.clear();
//                count = 0;
//            }
//            count++;
//        }
//        return (T) copy;
//    }

//    protected <T> T save(final T object) {
//        return IdentityExecutionUtils.runOperationByIdentifier(new IdentityOperation<T, RuntimeException>() {
//            @Override
//            public T execute() {
//                return genericEntityService.save(object);
//            }
//        }, getToSite(), getToCatalog());
//    }

    public Long getIdentifier(Object entity) {
        return (Long) genericEntityService.getIdentifier(entity);
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

    /**
     * Detects whether or not the current cloned entity is an extension of an entity in Broadleaf, and if so, if the
     * extension itself does not implement clone.
     *
     * @param cloned the cloned entity instance
     * @throws CloneNotSupportedException thrown if the entity is an extension and is does not implement clone
     */
    public void checkCloneable(Object cloned) throws CloneNotSupportedException {
        Method cloneMethod;
        try {
            cloneMethod = cloned.getClass().getMethod("createOrRetrieveCopyInstance", new Class[]{MultiTenantCopyContext.class});
        } catch (NoSuchMethodException e) {
            throw ExceptionHelper.refineException(e);
        }
        boolean cloneMethodLocal = false;
        for (String prefix : BROADLEAF_PACKAGE_PREFIXES) {
            if (cloneMethod.getDeclaringClass().getName().startsWith(prefix)) {
                cloneMethodLocal = true;
                break;
            }
        }
        boolean cloneClassLocal = false;
        for (String prefix : BROADLEAF_PACKAGE_PREFIXES) {
            if (cloned.getClass().getName().startsWith(prefix)) {
                cloneClassLocal = true;
                break;
            }
        }
        if (cloneMethodLocal && !cloneClassLocal) {
            //subclass is not implementing the clone method
            throw new CloneNotSupportedException("The system is attempting to clone " + cloned.getClass().getName() +
                    " and has determined the custom extension does not implement clone. This class should implement " +
                    "clone, and inside first call super.clone() to get back an instance of your class (" + cloned.getClass().getName() +
                    "), and then finish populating this instance with your custom fields before passing back the finished object.");
        }
    }

    /**
     * Create a new instance of the polymorphic entity type - could be an extended type outside of Broadleaf.
     *
     * @param instance the object instance for the actual entity type (could be extended)
     * @param <G>
     * @return the new, empty instance of the entity
     * @throws java.lang.CloneNotSupportedException
     */
    public <G> CreateResponse<G> createOrRetrieveCopyInstance(Object instance) throws CloneNotSupportedException {
        if (instance instanceof Status && 'Y' == ((Status) instance).getArchived()) {
            throw new CloneNotSupportedException("Attempting to clone an archived instance");
        }
        Class<?> instanceClass = instance.getClass();
        Object originalId = getIdentifier(instance);
        Object previousClone = getClonedVersion(instanceClass, originalId);
        G response;
        boolean alreadyPopulate;
        if (previousClone != null) {
            response = (G) previousClone;
            alreadyPopulate = true;
        } else {
            try {
                response = (G) instanceClass.newInstance();
            } catch (InstantiationException e) {
                throw ExceptionHelper.refineException(e);
            } catch (IllegalAccessException e) {
                throw ExceptionHelper.refineException(e);
            }
            checkCloneable(response);
            alreadyPopulate = false;
            currentEquivalentMap.put(response, getIdentifier(instance));
        }
        return new CreateResponse<G>(response, alreadyPopulate);
    }

    public Serializable getOriginalIdentifier(Object copy) {
        return currentEquivalentMap.get(copy);
    }

    public void clearOriginalIdentifiers() {
        currentEquivalentMap.clear();
    }

    public Object removeOriginalIdentifier(Object copy) {
        return currentEquivalentMap.remove(copy);
    }

    public void checkLevel1Cache() {
        if (count % 20 == 0) {
            count = 0;
            genericEntityService.flush();
            genericEntityService.clear();
        }
        count++;
    }
}
