/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FilterMapping;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.GlobalValidationResult;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


/**
 * @see org.broadleafcommerce.openadmin.server.security.service.RowLevelSecurityService
 * @author Phillip Verheyden (phillipuniverse)
 * @author Jeff Fischer
 */
@Service("blRowLevelSecurityService")
public class RowLevelSecurityServiceImpl implements RowLevelSecurityService, ExceptionAwareRowLevelSecurityProvider {
    
    private static final Log LOG = LogFactory.getLog(RowLevelSecurityServiceImpl.class);
    
    @Resource(name = "blRowLevelSecurityProviders")
    protected List<RowLevelSecurityProvider> providers;
    
    @Override
    public void addFetchRestrictions(AdminUser currentUser, String ceilingEntity, List<Predicate> restrictions, List<Order> sorts,
            Root entityRoot,
            CriteriaQuery criteria,
            CriteriaBuilder criteriaBuilder) {
        for (RowLevelSecurityProvider provider : getProviders()) {
            provider.addFetchRestrictions(currentUser, ceilingEntity, restrictions, sorts, entityRoot, criteria, criteriaBuilder);
        }
    }
    
    @Override
    public Class<Serializable> getFetchRestrictionRoot(AdminUser currentUser, Class<Serializable> ceilingEntity, List<FilterMapping> filterMappings) {
        Class<Serializable> root = null;
        for (RowLevelSecurityProvider provider : getProviders()) {
            Class<Serializable> providerRoot = provider.getFetchRestrictionRoot(currentUser, ceilingEntity, filterMappings);
            if (providerRoot != null) {
                root = providerRoot;
            }
        }
        
        return root;
    }

    @Override
    public boolean canUpdate(AdminUser currentUser, Entity entity) {
        for (RowLevelSecurityProvider provider : getProviders()) {
            if (!provider.canUpdate(currentUser, entity)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public EntityFormModifierConfiguration getUpdateDenialExceptions() {
        EntityFormModifierConfiguration sum = new EntityFormModifierConfiguration();
        for (RowLevelSecurityProvider provider : getProviders()) {
            if (provider instanceof ExceptionAwareRowLevelSecurityProvider) {
                EntityFormModifierConfiguration response = ((ExceptionAwareRowLevelSecurityProvider) provider).getUpdateDenialExceptions();
                if (response != null) {
                    if (!CollectionUtils.isEmpty(response.getModifier())) {
                        sum.getModifier().addAll(response.getModifier());
                    }
                    if (!CollectionUtils.isEmpty(response.getData())) {
                        sum.getData().addAll(response.getData());
                    }
                }
            }
        }
        return sum;
    }

    @Override
    public boolean canRemove(AdminUser currentUser, Entity entity) {
        for (RowLevelSecurityProvider provider : getProviders()) {
            if (!provider.canRemove(currentUser, entity)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canAdd(AdminUser currentUser, String sectionClassName, ClassMetadata cmd) {
        for (RowLevelSecurityProvider provider : getProviders()) {
            if (!provider.canAdd(currentUser, sectionClassName, cmd)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public GlobalValidationResult validateUpdateRequest(AdminUser currentUser, Entity entity, PersistencePackage persistencePackage) {
        GlobalValidationResult validationResult = new GlobalValidationResult(true);
        for (RowLevelSecurityProvider provider : getProviders()) {
            GlobalValidationResult providerValidation = provider.validateUpdateRequest(currentUser, entity, persistencePackage);
            if (providerValidation.isNotValid()) {
                validationResult.setValid(false);
                validationResult.addErrorMessage(providerValidation.getErrorMessage());
            }
        }
        return validationResult;
    }

    @Override
    public GlobalValidationResult validateRemoveRequest(AdminUser currentUser, Entity entity, PersistencePackage persistencePackage) {
        GlobalValidationResult validationResult = new GlobalValidationResult(true);
        for (RowLevelSecurityProvider provider : getProviders()) {
            GlobalValidationResult providerValidation = provider.validateRemoveRequest(currentUser, entity, persistencePackage);
            if (providerValidation.isNotValid()) {
                validationResult.setValid(false);
                validationResult.addErrorMessage(providerValidation.getErrorMessage());
            }
        }
        return validationResult;
    }

    @Override
    public GlobalValidationResult validateAddRequest(AdminUser currentUser, Entity entity, PersistencePackage persistencePackage) {
        GlobalValidationResult validationResult = new GlobalValidationResult(true);
        for (RowLevelSecurityProvider provider : getProviders()) {
            GlobalValidationResult providerValidation = provider.validateAddRequest(currentUser, entity,
                    persistencePackage);
            if (providerValidation.isNotValid()) {
                validationResult.setValid(false);
                validationResult.addErrorMessage(providerValidation.getErrorMessage());
            }
        }
        return validationResult;
    }

    @Override
    public List<RowLevelSecurityProvider> getProviders() {
        return providers;
    }
    
    public void setProviders(List<RowLevelSecurityProvider> providers) {
        this.providers = providers;
    }

}
