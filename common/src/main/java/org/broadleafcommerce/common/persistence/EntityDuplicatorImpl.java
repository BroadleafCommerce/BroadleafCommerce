/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
import org.broadleafcommerce.common.util.TransactionUtils;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.annotation.Resource;

import static org.broadleafcommerce.common.copy.MultiTenantCopyContext.MANUAL_DUPLICATION;
import static org.broadleafcommerce.common.copy.MultiTenantCopyContext.PROPAGATION;

/**
 * @author Jeff Fischer
 * @see EntityDuplicator
 */
@Service("blEntityDuplicator")
public class EntityDuplicatorImpl extends MultiTenantCopier implements EntityDuplicator {

    @Value("${admin.entity.duplication.isactive:false}")
    protected boolean isActive = false;

    @Resource(name = "blEntityDuplicatorExtensionManager")
    protected EntityDuplicatorExtensionManager extensionManager;

    @Resource(name = "blMultiTenantCopierExtensionManager")
    protected MultiTenantCopierExtensionManager mtCopierExtensionManager;

    @Resource(name = "blEntityDuplicationHelpers")
    protected Collection<EntityDuplicationHelper> entityDuplicationHelpers;

    @Override
    public void copyEntities(final MultiTenantCopyContext context) throws Exception {
        throw new UnsupportedOperationException("Not Supported");
    }

    @Override
    public boolean validate(final Class<?> entityClass, final Long id) {
        if (!isActive) {
            return false;
        }

        final Object entity = genericEntityService.readGenericEntity(entityClass, id);

        return validate(entity);
    }

    @Override
    public boolean validate(final Object entity) {
        if (!isActive) {
            return false;
        }

        if (!(entity instanceof MultiTenantCloneable)) {
            return false;
        }

        final Set<EntityDuplicationHelper> helpers = filterDuplicationHelpers((MultiTenantCloneable<?>) entity);

        if (helpers.isEmpty()) {
            return false;
        }

        ExtensionResultHolder<Boolean> response = new ExtensionResultHolder<>();
        response.setResult(true);

        if (extensionManager != null) {
            extensionManager.validateDuplicate(entity, response);
        }

        return response.getResult();
    }

    /**
     * Instead of passing in {@link EntityDuplicateModifier}s, add the beans to
     * {@code EntityDuplicationHelpers}. Additionally, add copy hints to the helpers.
     * Also, note that you should implement {@link AbstractEntityDuplicationHelper} now instead as
     * {@link EntityDuplicateModifier} is deprecated.
     */
    @Deprecated
    @Override
    public <T> T copy(
            final Class<T> entityClass,
            final Long id,
            final Map<String, String> copyHints,
            final EntityDuplicateModifier... modifiers
    ) {
        return copy(entityClass, id, copyHints);
    }

    /**
     * Instead of passing in {@link EntityDuplicateModifier}s, add the beans to
     * {@code EntityDuplicationHelpers}. Additionally, add copy hints to the helpers.
     * Also, note that you should implement {@link AbstractEntityDuplicationHelper} now instead as
     * {@link EntityDuplicateModifier} is deprecated.
     */
    @Deprecated
    @Override
    public <T> T copy(
            final MultiTenantCopyContext context,
            final MultiTenantCloneable<T> entity,
            final Map<String, String> copyHints,
            final EntityDuplicateModifier... modifiers
    ) {
        return copy(context, entity, copyHints);
    }

    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    @Override
    public <T> T copy(final Class<T> entityClass, final Long id) {
        genericEntityService.flush();
        genericEntityService.clear();

        final Object entity = genericEntityService.readGenericEntity(entityClass, id);

        if (!validate(entity)) {
            throw new IllegalArgumentException(
                    String.format("Entity not valid for duplication - %s:%s",
                            entityClass.getName(), id));
        }

        final T dup;

        try {
            final Site currentSite = BroadleafRequestContext.getBroadleafRequestContext().getNonPersistentSite();
            MultiTenantCopyContext context = new MultiTenantCopyContext(
                    null,
                    null,
                    currentSite,
                    currentSite,
                    genericEntityService,
                    mtCopierExtensionManager
            );

            if (extensionManager != null) {
                final ExtensionResultHolder<MultiTenantCopyContext> contextResponse =
                        new ExtensionResultHolder<>();
                extensionManager.setupDuplicate(entity, contextResponse);

                if (contextResponse.getResult() != null) {
                    context = contextResponse.getResult();
                }
            }

            context.getCopyHints().put(MANUAL_DUPLICATION, Boolean.TRUE.toString());
            dup = performCopy(context, (MultiTenantCloneable<T>) entity);

            ExtensionResultHolder<List<MultiTenantCopyContext>> resultHolder = new ExtensionResultHolder<>();
            extensionManager.getCatalogsForPropagation(context, resultHolder);
            List<MultiTenantCopyContext> contexts = resultHolder.getResult();
            for (MultiTenantCopyContext multiTenantCopyContext : contexts) {
                multiTenantCopyContext.getCopyHints().put(MANUAL_DUPLICATION, Boolean.TRUE.toString());
                BroadleafRequestContext.getBroadleafRequestContext().getAdditionalProperties()
                        .put("MANUAL_DUPLICATION_PROPAGATION", "TRUE");
                multiTenantCopyContext.getCopyHints().put(PROPAGATION, Boolean.TRUE.toString());
                performCopy(multiTenantCopyContext, (MultiTenantCloneable<T>) dup);
            }
        } catch (Exception e) {
            throw ExceptionHelper.refineException(RuntimeException.class, RuntimeException.class,
                    String.format("Unable to duplicate entity %s:%s", entityClass.getName(), id),
                    e);
        } finally {
            if (extensionManager != null) {
                extensionManager.tearDownDuplicate();
            }
        }

        return dup;
    }

    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    @Override
    public <T> T copy(final MultiTenantCopyContext context, final MultiTenantCloneable<T> entity) {
        if (!validate(entity)) {
            throw new IllegalArgumentException(
                    String.format("Entity not valid for duplication - %s:%s",
                            entity.getClass().getName(),
                            genericEntityService.getIdentifier(entity)));
        }

        final T dup;

        try {
            if (extensionManager != null) {
                ExtensionResultHolder<MultiTenantCopyContext> contextResponse = new ExtensionResultHolder<>();
                extensionManager.setupDuplicate(entity, contextResponse);
            }

            dup = performCopy(context, entity);
        } catch (Exception e) {
            throw ExceptionHelper.refineException(RuntimeException.class, RuntimeException.class,
                    String.format("Unable to duplicate entity %s:%s", entity.getClass().getName(),
                            genericEntityService.getIdentifier(entity)), e);
        } finally {
            if (extensionManager != null) {
                extensionManager.tearDownDuplicate();
            }
        }

        return dup;
    }

    /**
     * Instead of passing in {@link EntityDuplicateModifier}s, add the beans to
     * {@code EntityDuplicationHelpers}. Additionally, add copy hints to the helpers.
     * Also, note that you should implement {@link AbstractEntityDuplicationHelper} now instead as
     * {@link EntityDuplicateModifier} is deprecated.
     */
    @Deprecated
    protected <T> T performCopy(
            final MultiTenantCopyContext context,
            final MultiTenantCloneable<T> entity,
            Map<String, String> copyHints,
            final EntityDuplicateModifier... modifiers
    ) throws Exception {
        return performCopy(context, entity, copyHints);
    }

    protected <T> T performCopy(
            final MultiTenantCopyContext context,
            final MultiTenantCloneable<T> entity
    ) throws Exception {
        final Set<EntityDuplicationHelper> helpers = filterDuplicationHelpers(entity);
        putAllCopyHints(context, helpers);
        context.setForDuplicate(true);

        persistCopyObjectTree(new CopyOperation<T, CloneNotSupportedException>() {
            @Override
            public T execute(T original) throws CloneNotSupportedException {
                T response = entity.createOrRetrieveCopyInstance(context).getClone();
                for (final EntityDuplicationHelper helper : helpers) {
                    helper.modifyInitialDuplicateState(original, response, context);
                }
                return response;
            }
        }, (Class<T>) entity.getClass(), (T) entity, context);

        return context.getClonedVersion((Class<T>) entity.getClass(), genericEntityService.getIdentifier(entity));
    }

    protected Set<EntityDuplicationHelper> filterDuplicationHelpers(final MultiTenantCloneable entity) {
        final Set<EntityDuplicationHelper> filteredHelpers = new HashSet<>();

        for (final EntityDuplicationHelper helper : entityDuplicationHelpers) {
            if (helper.canHandle(entity)) {
                filteredHelpers.add(helper);
            }
        }

        return filteredHelpers;
    }

    protected void putAllCopyHints(final MultiTenantCopyContext context, final Set<EntityDuplicationHelper> helpers) {
        final Map<String, String> hints = context.getCopyHints();
        for (final EntityDuplicationHelper helper : helpers) {
            hints.putAll(helper.getCopyHints());
        }
    }

}
