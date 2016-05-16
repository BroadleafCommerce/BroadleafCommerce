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

import org.apache.commons.lang.ArrayUtils;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.service.GenericEntityService;
import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.util.tenant.IdentityExecutionUtils;
import org.broadleafcommerce.common.util.tenant.IdentityOperation;
import org.broadleafcommerce.common.web.BroadleafRequestContext;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Embeddable;

public class MultiTenantCopyContext {

    public static final String[] BROADLEAF_PACKAGE_PREFIXES = {"org.broadleafcommerce","com.broadleafcommerce"};

    protected Catalog fromCatalog;
    protected Catalog toCatalog;
    protected Site fromSite;
    protected Site toSite;
    protected MultiTenantCopierExtensionManager extensionManager;
    protected BiMap<Integer, String> currentEquivalentMap = HashBiMap.create();
    protected Map<Integer, Object> currentCloneMap = new HashMap<Integer, Object>();
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
     * Create a new instance of the polymorphic entity type - could be an extended type outside of Broadleaf.
     *
     * @param instance the object instance for the actual entity type (could be extended)
     * @param <G>
     * @return the new, empty instance of the entity
     * @throws java.lang.CloneNotSupportedException
     */
    public <G> CreateResponse<G> createOrRetrieveCopyInstance(Object instance) throws CloneNotSupportedException {
        CreateResponse<G> createResponse;
        BroadleafRequestContext context = setupContext();
        validateOriginal(instance);
        Class<?> instanceClass = instance.getClass();
        createResponse = handleEmbedded(instanceClass);
        if (createResponse == null) {
            createResponse = handleStandardEntity(instance, context, instanceClass);
        }
        tearDownContext(context);
        return createResponse;
    }

    public void clearOriginalIdentifiers() {
        currentEquivalentMap.clear();
        currentCloneMap.clear();
    }

    public Object removeOriginalIdentifier(Object copy) {
        if (currentEquivalentMap.containsKey(System.identityHashCode(copy))) {
            currentCloneMap.remove(System.identityHashCode(copy));
            String valKey = currentEquivalentMap.remove(System.identityHashCode(copy));
            return Long.parseLong(valKey.substring(valKey.indexOf("_") + 1, valKey.length()));
        }
        return null;
    }

    public Field[] getAllFields(Class<?> targetClass) {
        Field[] allFields = new Field[]{};
        boolean eof = false;
        Class<?> currentClass = targetClass;
        while (!eof) {
            Field[] fields = currentClass.getDeclaredFields();
            allFields = (Field[]) ArrayUtils.addAll(allFields, fields);
            if (currentClass.getSuperclass() != null) {
                currentClass = currentClass.getSuperclass();
            } else {
                eof = true;
            }
        }

        return allFields;
    }

    public Object getPreviousClone(Class<?> instanceClass, Long originalId) {
        Object previousClone;
        if (currentEquivalentMap.inverse().containsKey(instanceClass.getName() + "_" + originalId)) {
            previousClone = currentCloneMap.get(currentEquivalentMap.inverse().get(instanceClass.getName() + "_" + originalId));
        } else {
            previousClone = getClonedVersion(instanceClass, originalId);
        }
        return previousClone;
    }

    protected boolean checkCloneStatus(Object instance) {
        boolean shouldClone = true;
        ExtensionResultHolder<Boolean> shouldCloneHolder = new ExtensionResultHolder<Boolean>();
        if (extensionManager != null) {
            ExtensionResultStatusType status = extensionManager.getProxy().shouldClone(this, instance,
                    shouldCloneHolder);
            if (ExtensionResultStatusType.NOT_HANDLED != status) {
                shouldClone = shouldCloneHolder.getResult();
            }
        }
        return shouldClone;
    }

    protected void validateOriginal(Object instance) throws CloneNotSupportedException {
        if (instance instanceof Status && 'Y' == ((Status) instance).getArchived()) {
            throw new CloneNotSupportedException("Attempting to clone an archived instance");
        }
    }

    protected void tearDownContext(BroadleafRequestContext context) {
        context.setCurrentCatalog(getFromCatalog());
        context.setCurrentProfile(getFromSite());
        context.setSite(getFromSite());
    }

    protected BroadleafRequestContext setupContext() {
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        context.setCurrentCatalog(getToCatalog());
        context.setCurrentProfile(getToSite());
        context.setSite(getToSite());
        return context;
    }

    protected <G> G createNewInstance(Class<?> instanceClass) {
        G response;
        try {
            response = (G) instanceClass.newInstance();
        } catch (InstantiationException e) {
            throw ExceptionHelper.refineException(e);
        } catch (IllegalAccessException e) {
            throw ExceptionHelper.refineException(e);
        }
        return response;
    }

    protected <G> CreateResponse<G> handleStandardEntity(Object instance, BroadleafRequestContext context, Class<?> instanceClass) throws CloneNotSupportedException {
        CreateResponse<G> createResponse;
        Long originalId = getIdentifier(instance);
        Object previousClone = getPreviousClone(instanceClass, originalId);
        G response;
        boolean alreadyPopulate;
        if (previousClone != null) {
            response = (G) previousClone;
            alreadyPopulate = true;
        } else {
            boolean shouldClone = checkCloneStatus(instance);
            if (!shouldClone) {
                response = (G) instance;
                alreadyPopulate = true;
            } else {
                alreadyPopulate = false;
                response = performCopy(instance, instanceClass, originalId);
            }
        }
        createResponse = new CreateResponse<G>(response, alreadyPopulate);
        return createResponse;
    }

    protected <G> CreateResponse<G> handleEmbedded(Class<?> instanceClass) {
        CreateResponse<G> createResponse = null;
        if (instanceClass.getAnnotation(Embeddable.class) != null) {
            G response = createNewInstance(instanceClass);
            createResponse = new CreateResponse<G>(response, false);
        }
        return createResponse;
    }

    protected <G> G performCopy(Object instance, Class<?> instanceClass, Long originalId) throws CloneNotSupportedException {
        G response = createNewInstance(instanceClass);
        validateClone(response);
        currentEquivalentMap.put(System.identityHashCode(response), instanceClass.getName() + "_" + originalId);
        currentCloneMap.put(System.identityHashCode(response), response);
        try {
            for (Field field : getAllFields(instanceClass)) {
                field.setAccessible(true);
                if (field.getType().getAnnotation(Embeddable.class) != null && MultiTenantCloneable.class.isAssignableFrom(field.getType())) {
                    Object embeddable = field.get(instance);
                    if (embeddable != null) {
                        field.set(response, ((MultiTenantCloneable) embeddable).createOrRetrieveCopyInstance(this).getClone());
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw ExceptionHelper.refineException(e);
        }
        return response;
    }

    /**
     * Detects whether or not the current cloned entity is an extension of an entity in Broadleaf, and if so, if the
     * extension itself does not implement clone.
     *
     * @param cloned the cloned entity instance
     * @throws CloneNotSupportedException thrown if the entity is an extension and is does not implement clone
     */
    protected void validateClone(Object cloned) throws CloneNotSupportedException {
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
}
