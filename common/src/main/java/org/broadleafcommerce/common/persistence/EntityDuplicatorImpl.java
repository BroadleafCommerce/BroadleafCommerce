/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2018 Broadleaf Commerce
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
package org.broadleafcommerce.common.persistence;

import org.broadleafcommerce.common.copy.CopyOperation;
import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.common.copy.MultiTenantCopier;
import org.broadleafcommerce.common.copy.MultiTenantCopierExtensionManager;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

import javax.annotation.Resource;

/**
 * @see EntityDuplicator
 * @author Jeff Fischer
 */
@Service("blEntityDuplicator")
public class EntityDuplicatorImpl extends MultiTenantCopier implements EntityDuplicator {

    @Value("${admin.entity.duplication.isactive:false}")
    protected boolean isActive = false;

    @Resource(name = "blEntityDuplicatorExtensionManager")
    protected EntityDuplicatorExtensionManager extensionManager;

    @Resource(name = "blMultiTenantCopierExtensionManager")
    protected MultiTenantCopierExtensionManager mtCopierExtensionManager;

    @Override
    public void copyEntities(final MultiTenantCopyContext context) throws Exception {
        throw new UnsupportedOperationException("Not Supported");
    }

    @Override
    public boolean validate(Class<?> entityClass, Long id) {
        if (!isActive) {
            return false;
        }
        Object entity = genericEntityService.readGenericEntity(entityClass, id);
        return validate(entity);
    }

    @Override
    public boolean validate(Object entity) {
        if (!isActive) {
            return false;
        }
        ExtensionResultHolder<Boolean> response = new ExtensionResultHolder<Boolean>();
        response.setResult(true);
        if (extensionManager != null) {
            extensionManager.validateDuplicate(entity, response);
        }
        return response.getResult();
    }

    @Override
    public <T> T copy(Class<T> entityClass, Long id, Map<String, String> copyHints, EntityDuplicateModifier... modifiers) {
        genericEntityService.flush();
        genericEntityService.clear();
        Object entity = genericEntityService.readGenericEntity(entityClass, id);
        if (!(entity instanceof MultiTenantCloneable)) {
            IllegalArgumentException e = new IllegalArgumentException("Copying is only supported for classes implementing MultiTenantCloneable");
            LOG.error(String.format("Unable to duplicate entity %s:%s", entityClass.getName(), id), e);
            throw e;
        }
        boolean isValid = validate(entity);
        T dup;
        if (isValid) {
            try {
                Site currentSite = BroadleafRequestContext.getBroadleafRequestContext().getNonPersistentSite();
                MultiTenantCopyContext context = new MultiTenantCopyContext(null, null, currentSite, currentSite, genericEntityService, mtCopierExtensionManager);
                if (extensionManager != null) {
                    ExtensionResultHolder<MultiTenantCopyContext> contextResponse = new ExtensionResultHolder<MultiTenantCopyContext>();
                    extensionManager.setupDuplicate(entity, contextResponse);
                    if (contextResponse.getResult() != null) {
                        context = contextResponse.getResult();
                    }
                }
                dup = performCopy(context, (MultiTenantCloneable<T>) entity, copyHints, modifiers);
            } catch (Exception e) {
                LOG.error(String.format("Unable to duplicate entity %s:%s", entityClass.getName(), id), e);
                throw ExceptionHelper.refineException(e);
            } finally {
                if (extensionManager != null) {
                    extensionManager.tearDownDuplicate();
                }
            }
        } else {
            LOG.error(String.format("Entity not valid for duplication - %s:%s", entityClass.getName(), id));
            throw new IllegalArgumentException(String.format("Entity not valid for duplication - %s:%s", entityClass.getName(), id));
        }
        return dup;
    }

    @Override
    public <T> T copy(final MultiTenantCopyContext context, final MultiTenantCloneable<T> entity, Map<String, String> copyHints, final EntityDuplicateModifier... modifiers) {
        if (!(entity instanceof MultiTenantCloneable)) {
            IllegalArgumentException e = new IllegalArgumentException("Copying is only supported for classes implementing MultiTenantCloneable");
            LOG.error(String.format("Unable to duplicate entity %s:%s", entity.getClass().getName(), genericEntityService.getIdentifier(entity)), e);
            throw e;
        }
        boolean isValid = validate(entity);
        T dup;
        if (isValid) {
            try {
                if (extensionManager != null) {
                    ExtensionResultHolder<MultiTenantCopyContext> contextResponse = new ExtensionResultHolder<MultiTenantCopyContext>();
                    extensionManager.setupDuplicate(entity, contextResponse);
                }
                dup = performCopy(context, entity, copyHints, modifiers);
            } catch (Exception e) {
                LOG.error(String.format("Unable to duplicate entity %s:%s", entity.getClass().getName(), genericEntityService.getIdentifier(entity)), e);
                throw ExceptionHelper.refineException(e);
            } finally {
                if (extensionManager != null) {
                    extensionManager.tearDownDuplicate();
                }
            }
        } else {
            LOG.error(String.format("Entity not valid for duplication - %s:%s", entity.getClass().getName(), genericEntityService.getIdentifier(entity)));
            throw new IllegalArgumentException(String.format("Entity not valid for duplication - %s:%s", entity.getClass().getName(), genericEntityService.getIdentifier(entity)));
        }
        return dup;
    }

    protected <T> T performCopy(final MultiTenantCopyContext context, final MultiTenantCloneable<T> entity, Map<String, String> copyHints, final EntityDuplicateModifier... modifiers) throws Exception {
        context.getCopyHints().putAll(copyHints);
        context.setForDuplicate(true);
        persistCopyObjectTree(new CopyOperation<T, CloneNotSupportedException>() {
            @Override
            public T execute(T original) throws CloneNotSupportedException {
                T response = entity.createOrRetrieveCopyInstance(context).getClone();
                for (EntityDuplicateModifier modifier : modifiers) {
                    modifier.modifyInitialDuplicateState(response);
                }
                return response;
            }
        }, (Class<T>) entity.getClass(), (T) entity, context);
        return context.getClonedVersion((Class<T>) entity.getClass(), genericEntityService.getIdentifier(entity));
    }
}
