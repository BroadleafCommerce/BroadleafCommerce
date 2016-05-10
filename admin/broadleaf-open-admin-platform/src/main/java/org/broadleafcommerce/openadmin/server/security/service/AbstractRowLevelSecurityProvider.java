/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.server.security.service;

import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FilterMapping;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.GlobalValidationResult;

import java.io.Serializable;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Dummy implementation of a {@link RowLevelSecurityProvider}. Implementors should extend this class
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @author Jeff Fischer
 */
public class AbstractRowLevelSecurityProvider implements RowLevelSecurityProvider {

    @Override
    public void addFetchRestrictions(AdminUser currentUser, String ceilingEntity, List<Predicate> restrictions, List<Order> sorts, Root entityRoot, CriteriaQuery criteria, CriteriaBuilder criteriaBuilder) {
        // intentionally unimplemented
    }

    @Override
    public Class<Serializable> getFetchRestrictionRoot(AdminUser currentUser, Class<Serializable> ceilingEntity, List<FilterMapping> filterMappings) {
        return null;
    }

    @Override
    public boolean canUpdate(AdminUser currentUser, Entity entity) {
        return true;
    }

    @Override
    public boolean canRemove(AdminUser currentUser, Entity entity) {
        return true;
    }

    @Override
    public boolean canAdd(AdminUser currentUser, String sectionClassName, ClassMetadata cmd) {
        return true;
    }

    @Override
    public GlobalValidationResult validateAddRequest(AdminUser currentUser, Entity entity, PersistencePackage persistencePackage) {
        return new GlobalValidationResult(true);
    }

    @Override
    public GlobalValidationResult validateUpdateRequest(AdminUser currentUser, Entity entity, PersistencePackage persistencePackage) {
        return new GlobalValidationResult(true);
    }

    @Override
    public GlobalValidationResult validateRemoveRequest(AdminUser currentUser, Entity entity, PersistencePackage persistencePackage) {
        return new GlobalValidationResult(true);
    }

}
