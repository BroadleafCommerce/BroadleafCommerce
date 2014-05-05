/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.security.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 * 
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@Service("blRowLevelSecurityService")
public class RowLevelSecurityServiceImpl implements RowLevelSecurityService {
    
    private static final Log LOG = LogFactory.getLog(RowLevelSecurityServiceImpl.class);
    
    @Resource(name = "blRowLevelSecurityProviders")
    List<RowLevelSecurityProvider> providers;
    
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
    public boolean canRemove(AdminUser currentUser, Entity entity) {
        for (RowLevelSecurityProvider provider : getProviders()) {
            if (!provider.canRemove(currentUser, entity)) {
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
    public List<RowLevelSecurityProvider> getProviders() {
        return providers;
    }
    
    public void setProviders(List<RowLevelSecurityProvider> providers) {
        this.providers = providers;
    }

}
