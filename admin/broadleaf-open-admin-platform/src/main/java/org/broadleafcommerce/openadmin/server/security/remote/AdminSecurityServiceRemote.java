/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2026 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.server.security.remote;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.SecurityServiceException;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.security.service.ExploitProtectionService;
import org.broadleafcommerce.common.util.StringUtil;
import org.broadleafcommerce.common.web.SandBoxContext;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.dto.SectionCrumb;
import org.broadleafcommerce.openadmin.server.security.domain.AdminPermission;
import org.broadleafcommerce.openadmin.server.security.domain.AdminRole;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.service.RowLevelSecurityService;
import org.broadleafcommerce.openadmin.server.security.service.type.PermissionType;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManagerFactory;
import org.broadleafcommerce.openadmin.server.service.ValidationException;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.GlobalValidationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

/**
 * Service for handeling security with Ajax components.  Serves two functions.
 * <ul>
 *     <li>
 *         Converts the ServerSide AdminUser to a client level admin user with
 *         appropriate roles defined.
 *     </li>
 *     <li>
 *         Provides a method to check if the current logged in user matches the
 *         client side user and verifies whether that user has access to the
 *         entity operation they are trying to perform.
 *     </li>
 * </ul>
 * 1.
 * @author jfischer
 *
 */
@Service("blAdminSecurityRemoteService")
public class AdminSecurityServiceRemote implements AdminSecurityService, SecurityVerifier {
    
    public static final String ANONYMOUS_USER_NAME = "anonymousUser";
    private static final Log LOG = LogFactory.getLog(AdminSecurityServiceRemote.class);
    
    @Resource(name="blAdminSecurityService")
    protected org.broadleafcommerce.openadmin.server.security.service.AdminSecurityService securityService;

    @Resource(name="blExploitProtectionService")
    protected ExploitProtectionService exploitProtectionService;
    
    @Resource(name = "blRowLevelSecurityService")
    protected RowLevelSecurityService rowLevelSecurityService;

    /**
     * Defensive ceiling on {@link #crumbRelationshipCache}. Crumb identifiers are validated against the admin
     * section / persistable-entity whitelist before they reach this service, so the key space is already bounded in
     * practice; this cap guarantees the cache cannot grow without bound regardless of how it is populated.
     */
    protected static final int MAX_CRUMB_RELATIONSHIP_CACHE_SIZE = 1000;

    /**
     * Resolved {@link CrumbRelationship}s keyed by "parentClass->childClass". Entity mappings don't change at runtime,
     * so entries never need eviction for correctness; {@link #MAX_CRUMB_RELATIONSHIP_CACHE_SIZE} only bounds growth.
     */
    protected final Map<String, Set<CrumbRelationship>> crumbRelationshipCache =
            new ConcurrentHashMap<String, Set<CrumbRelationship>>();

    @Override
    public org.broadleafcommerce.openadmin.server.security.remote.AdminUser getAdminUser() throws ServiceException {
        AdminUser persistentAdminUser = getPersistentAdminUser();
        if (persistentAdminUser != null) {
            org.broadleafcommerce.openadmin.server.security.remote.AdminUser response = new org.broadleafcommerce.openadmin.server.security.remote.AdminUser();
            for (AdminRole role : persistentAdminUser.getAllRoles()) {
                response.getRoles().add(role.getName());
                for (AdminPermission permission : role.getAllPermissions()) {
                    response.getPermissions().add(permission.getName());
                }
            }
            for (AdminPermission permission : persistentAdminUser.getAllPermissions()) {
                response.getPermissions().add(permission.getName());
            }
            response.setUserName(persistentAdminUser.getLogin());
            response.setCurrentSandBoxId(String.valueOf(SandBoxContext.getSandBoxContext().getSandBoxId()));
            response.setEmail(persistentAdminUser.getEmail());
            response.setName(persistentAdminUser.getName());
            response.setPhoneNumber(persistentAdminUser.getPhoneNumber());
            response.setId(persistentAdminUser.getId());
            return response;
        }

        return null;
    }

    @Override
    public AdminUser getPersistentAdminUser() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        if (ctx != null) {
            Authentication auth = ctx.getAuthentication();
            if (auth != null && !auth.getName().equals(ANONYMOUS_USER_NAME)) {
                UserDetails temp = (UserDetails) auth.getPrincipal();

                return securityService.readAdminUserByUserName(temp.getUsername());
            }
        }

        return null;
    }

    @Override
    public void securityCheck(PersistencePackage persistencePackage, EntityOperationType operationType) throws ServiceException {
        // LinkedHashSet keeps the request's own target first, which the "unregistered ceiling entity" warning reads
        Set<String> ceilingNames = new LinkedHashSet<String>();
        ceilingNames.add(persistencePackage.getSecurityCeilingEntityFullyQualifiedClassname());
        ceilingNames.addAll(getRelatedSectionCrumbCeilings(persistencePackage, operationType));

        Entity entity = persistencePackage.getEntity();

        if (persistencePackage.getPersistencePerspectiveItems().containsKey(PersistencePerspectiveItemType.ADORNEDTARGETLIST)) {
            if (persistencePackage.getEntity() != null) {
                for (Property property : persistencePackage.getProperties()) {
                    if (property.getName() != null && property.getName().endsWith(".id")
                            && property.getValue() == null) {
                        entity.addGlobalValidationError("adornedTargetRequired");
                        throw new ValidationException(entity);
                    }
                }
            }
        }

        GlobalValidationResult globalValidationResult = null;
        if (operationType.equals(EntityOperationType.UPDATE)) {
            globalValidationResult = rowLevelSecurityService.validateUpdateRequest(getPersistentAdminUser(), entity, persistencePackage);
        } else if (operationType.equals(EntityOperationType.REMOVE)) {
            globalValidationResult = rowLevelSecurityService.validateRemoveRequest(getPersistentAdminUser(), entity, persistencePackage);
        } else if (operationType.equals(EntityOperationType.ADD)) {
            globalValidationResult = rowLevelSecurityService.validateAddRequest(getPersistentAdminUser(), entity, persistencePackage);
        }
        
        if (globalValidationResult != null) {
            if (!globalValidationResult.isValid()) {
                if (StringUtils.isEmpty(globalValidationResult.getErrorMessage())) {
                    entity.addGlobalValidationError("rowLevelSecurityFailed");
                } else {
                    entity.addGlobalValidationErrors(globalValidationResult.getErrorMessages());
                }
                
                throw new ValidationException(entity, "Row level security check failed for " + operationType);
            }
        }
        
        securityCheck(ceilingNames.toArray(new String[ceilingNames.size()]), operationType);
    }

    @Override
    public void securityCheck(String ceilingEntityFullyQualifiedName, EntityOperationType operationType) throws ServiceException {
        securityCheck(new String[]{ceilingEntityFullyQualifiedName}, operationType);
    }


    protected void securityCheck(String[] ceilingNames, EntityOperationType operationType) throws ServiceException {
        if (ArrayUtils.isEmpty(ceilingNames)) {
            throw new SecurityServiceException("Security Check Failed: ceilingNames not specified");
        }
        AdminUser persistentAdminUser = getPersistentAdminUser();
        PermissionType permissionType;
        switch(operationType){
            case ADD:
                permissionType = PermissionType.CREATE;
                break;
            case FETCH:
                permissionType = PermissionType.READ;
                break;
            case REMOVE:
                permissionType = PermissionType.DELETE;
                break;
            case UPDATE:
                permissionType = PermissionType.UPDATE;
                break;
            case INSPECT:
                permissionType = PermissionType.READ;
                break;
            default:
                permissionType = PermissionType.OTHER;
                break;
        }
        SecurityServiceException primaryException = null;
        boolean isQualified = false;
        for (String ceilingEntityFullyQualifiedName : ceilingNames) {
            isQualified = securityService.isUserQualifiedForOperationOnCeilingEntity(persistentAdminUser, permissionType, ceilingEntityFullyQualifiedName);
            if (!isQualified){
                if (primaryException == null) {
                    primaryException = new SecurityServiceException("Security Check Failed for entity operation: " + operationType.toString() + " (" + ceilingEntityFullyQualifiedName + ")");
                }
            } else {
                break;
            }
        }
        if (!isQualified) {
            //check if the requested entity is not configured and warn
            if (!securityService.doesOperationExistForCeilingEntity(permissionType, ceilingNames[0])) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Detected security request for an unregistered ceiling entity (" + StringUtil.sanitize(ceilingNames[0]) + "). " +
                        "As a result, the request failed. Please make sure to configure security for any ceiling entities " +
                        "referenced via the admin. This is usually accomplished by adding records in the " +
                        "BLC_ADMIN_PERMISSION_ENTITY table. Note, depending on how the entity in question is used, you " +
                        "may need to add to BLC_ADMIN_PERMISSION, BLC_ADMIN_ROLE_PERMISSION_XREF and BLC_ADMIN_SEC_PERM_XREF.", primaryException);
                }
            }
            throw primaryException;
        }
    }

    /**
     * Returns the section crumb entities that may additionally authorize this request.
     * <p>
     * {@code sectionCrumbs} come straight from the client, so a crumb must never authorize a target it has nothing to
     * do with (otherwise a permission on any one entity becomes a permission on every entity). A crumb only counts when
     * the request target is genuinely reachable from it through mapped JPA relationships. Starting from the innermost
     * crumb, each crumb must reach the next one inwards (and the innermost one the target) as allowed by
     * {@link #isCrumbHopAllowed}; the walk stops at the first crumb that doesn't, so neither an unrelated crumb nor
     * anything in front of it is used.
     * <p>
     * This keeps the legitimate uses of crumbs working: editing an entity's own collections (Product -> Skus), nested
     * collection modals (Product -> Sku -> Sku media), adorned target collections (Product -> categories via
     * CategoryProductXref) and read-only to-one lookups (Product.defaultCategory -> Category).
     */
    protected List<String> getRelatedSectionCrumbCeilings(PersistencePackage persistencePackage,
                                                          EntityOperationType operationType) {
        List<String> related = new ArrayList<>();
        SectionCrumb[] crumbs = persistencePackage.getSectionCrumbs();
        if (ArrayUtils.isEmpty(crumbs)) {
            return related;
        }
        boolean readOnly = isReadOperation(operationType);
        // Both are server-side signals (built from the owning entity's field metadata, never from request params)
        // that this package is an adorned target collection request, where the ceiling is the target behind the xref.
        boolean adornedRequest = persistencePackage.getPersistencePerspectiveItems()
                .containsKey(PersistencePerspectiveItemType.ADORNEDTARGETLIST)
                || StringUtils.isNotEmpty(persistencePackage.getSectionEntityField());
        String child = persistencePackage.getSecurityCeilingEntityFullyQualifiedClassname();
        boolean finalHop = true;
        for (int i = crumbs.length - 1; i >= 0; i--) {
            String parent = crumbs[i].getSectionIdentifier();
            if (StringUtils.isBlank(parent) || StringUtils.isBlank(child)) {
                break;
            }
            if (!isSameEntity(parent, child)) {
                Set<CrumbRelationship> relationships = getCrumbRelationships(parent, child);
                boolean allowed = false;
                for (CrumbRelationship relationship : relationships) {
                    allowed |= isCrumbHopAllowed(relationship, readOnly, finalHop, adornedRequest);
                }
                if (!allowed) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Ignoring section crumb " + StringUtil.sanitize(parent) + " (and any crumbs before it)"
                                + " for " + operationType + " on " + StringUtil.sanitize(child)
                                + ": relationships " + relationships + " do not permit it");
                    }
                    break;
                }
                child = parent;
            }
            // Only the hop from the innermost crumb straight to the target gets the lookup/adorned allowances. A crumb
            // for the target itself (e.g. the owning section appended server-side) uses it up as well, so a crumb in
            // front of it can't use them to reach the target's own lookups or adorned collections.
            finalHop = false;
            related.add(parent);
        }
        return related;
    }

    /**
     * Whether one hop of the crumb chain may authorize the operation.
     *
     * @param finalHop       whether this hop reaches the request's own target (as opposed to a crumb nested inside it)
     * @param adornedRequest whether the server built this package for an adorned target collection
     */
    protected boolean isCrumbHopAllowed(CrumbRelationship relationship, boolean readOnly, boolean finalHop,
                                        boolean adornedRequest) {
        switch (relationship) {
            case COLLECTION:
                // the child is part of the parent's own collection/map: editing it is editing the parent
                return true;
            case JOIN_ENTITY:
                // parent -> collection of xref -> to-one target, i.e. an adorned target collection
                return finalHop && adornedRequest;
            case TO_ONE:
                // a foreign-key lookup: the parent may read the entities it can point at, never change them
                return finalHop && readOnly;
            default:
                return false;
        }
    }

    protected boolean isReadOperation(EntityOperationType operationType) {
        return operationType == EntityOperationType.FETCH || operationType == EntityOperationType.INSPECT;
    }

    /**
     * The thread context class loader can be null (e.g. when the call originates from a bootstrap or container-managed
     * thread). Loading a class with a null loader resolves against the bootstrap loader, which cannot see application
     * entity classes, so a legitimate crumb would be silently dropped. Fall back to a loader that can see them.
     */
    protected ClassLoader getEntityClassLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = getClass().getClassLoader();
        }
        if (cl == null) {
            cl = ClassLoader.getSystemClassLoader();
        }
        return cl;
    }

    protected boolean isSameEntity(String parent, String child) {
        if (parent.equals(child)) {
            return true;
        }
        try {
            // Load without initializing: these names are (validated) client input, so don't run static initializers.
            ClassLoader cl = getEntityClassLoader();
            Class<?> parentClass = Class.forName(parent, false, cl);
            Class<?> childClass = Class.forName(child, false, cl);
            return parentClass.isAssignableFrom(childClass) || childClass.isAssignableFrom(parentClass);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    protected Set<CrumbRelationship> getCrumbRelationships(String parent, String child) {
        String key = parent + "->" + child;
        Set<CrumbRelationship> relationships = crumbRelationshipCache.get(key);
        if (relationships == null) {
            relationships = resolveCrumbRelationships(parent, child);
            // An entry is an immutable fact about the mapping, so a racing recompute is harmless; the size guard is
            // all that is needed to keep the cache bounded.
            if (crumbRelationshipCache.size() < MAX_CRUMB_RELATIONSHIP_CACHE_SIZE) {
                crumbRelationshipCache.putIfAbsent(key, relationships);
            }
        }
        return relationships;
    }

    /**
     * Determines from the JPA metamodel every kind of relationship any polymorphic implementation of {@code parent}
     * has to {@code child} (an entity can have several, e.g. Product has both the to-one {@code defaultCategory} and
     * the {@code allParentCategoryXrefs} join collection to Category). Anything that can't be resolved (non-entity
     * ceilings, unknown classes) yields an empty set, i.e. the crumb is not trusted.
     */
    protected Set<CrumbRelationship> resolveCrumbRelationships(String parent, String child) {
        Set<CrumbRelationship> found = EnumSet.noneOf(CrumbRelationship.class);
        try {
            // Load without initializing: these names are (validated) client input, so don't run static initializers.
            ClassLoader cl = getEntityClassLoader();
            Class<?> childClass = Class.forName(child, false, cl);
            DynamicEntityDao dynamicEntityDao = PersistenceManagerFactory.getPersistenceManager(parent)
                    .getDynamicEntityDao();
            Metamodel metamodel = dynamicEntityDao.getStandardEntityManager().getMetamodel();
            for (Class<?> parentEntity : dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(
                    Class.forName(parent, false, cl))) {
                ManagedType<?> parentType;
                try {
                    parentType = metamodel.managedType(parentEntity);
                } catch (IllegalArgumentException e) {
                    continue;
                }
                findRelationships(metamodel, parentType, childClass, found, new HashSet<>());
            }
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Could not resolve the relationship from " + StringUtil.sanitize(parent) + " to "
                        + StringUtil.sanitize(child) + "; the section crumb will not be trusted", e);
            }
        }
        return found;
    }

    protected void findRelationships(Metamodel metamodel, ManagedType<?> type, Class<?> child,
                                     Set<CrumbRelationship> found, Set<Class<?>> visitedEmbeddables) {
        for (Attribute<?, ?> attribute : type.getAttributes()) {
            switch (attribute.getPersistentAttributeType()) {
                case ONE_TO_MANY:
                case MANY_TO_MANY:
                case ELEMENT_COLLECTION:
                    Class<?> element = ((PluralAttribute<?, ?, ?>) attribute).getElementType().getJavaType();
                    if (isRelated(element, child)) {
                        found.add(CrumbRelationship.COLLECTION);
                    } else if (hasToOne(metamodel, element, child)) {
                        found.add(CrumbRelationship.JOIN_ENTITY);
                    }
                    break;
                case MANY_TO_ONE:
                case ONE_TO_ONE:
                    if (isRelated(((SingularAttribute<?, ?>) attribute).getType().getJavaType(), child)) {
                        found.add(CrumbRelationship.TO_ONE);
                    }
                    break;
                case EMBEDDED:
                    Class<?> embeddable = attribute.getJavaType();
                    if (embeddable != null && visitedEmbeddables.add(embeddable)) {
                        findRelationships(metamodel, metamodel.managedType(embeddable), child, found,
                                visitedEmbeddables);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    protected boolean hasToOne(Metamodel metamodel, Class<?> entity, Class<?> child) {
        return hasToOne(metamodel, entity, child, new HashSet<Class<?>>());
    }

    protected boolean hasToOne(Metamodel metamodel, Class<?> entity, Class<?> child, Set<Class<?>> visitedEmbeddables) {
        if (entity == null) {
            return false;
        }
        ManagedType<?> type;
        try {
            type = metamodel.managedType(entity);
        } catch (IllegalArgumentException e) {
            return false;
        }
        for (Attribute<?, ?> attribute : type.getAttributes()) {
            Attribute.PersistentAttributeType attributeType = attribute.getPersistentAttributeType();
            if ((attributeType == Attribute.PersistentAttributeType.MANY_TO_ONE
                    || attributeType == Attribute.PersistentAttributeType.ONE_TO_ONE)
                    && isRelated(((SingularAttribute<?, ?>) attribute).getType().getJavaType(), child)) {
                return true;
            }
            // A join entity may hold its to-one target inside an @Embedded, so recurse into embeddables too.
            if (attributeType == Attribute.PersistentAttributeType.EMBEDDED) {
                Class<?> embeddable = attribute.getJavaType();
                if (embeddable != null && visitedEmbeddables.add(embeddable)
                        && hasToOne(metamodel, embeddable, child, visitedEmbeddables)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isRelated(Class<?> mapped, Class<?> child) {
        return mapped != null && mapped != Object.class
                && (child.isAssignableFrom(mapped) || mapped.isAssignableFrom(child));
    }

    /**
     * How a section crumb's entity relates to the next entity inwards.
     */
    protected enum CrumbRelationship {
        /** a ManyToOne/OneToOne to the child: a foreign-key lookup */
        TO_ONE,
        /** a collection of an entity that has a to-one to the child: an adorned target collection's xref */
        JOIN_ENTITY,
        /** the child is the element (or map value) of one of the parent's own collections */
        COLLECTION
    }
}
