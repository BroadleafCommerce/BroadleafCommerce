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
package org.broadleafcommerce.openadmin.server.service.persistence;

import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.GlobalValidationResult;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;


/**
 * 
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@Service("blRowLevelSecurityService")
public class RowLevelSecurityServiceImpl implements RowLevelSecurityService {

    @Override
    public void addFetchRestrictions(AdminUser user, String ceilingEntity, Root entityRoot, CriteriaQuery criteria, CriteriaBuilder criteriaBuilder) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean canUpdate(AdminUser user, Entity entity) {
        return true;
    }

    @Override
    public boolean canRemove(AdminUser user, Entity entity) {
        return true;
    }

    @Override
    public GlobalValidationResult validateUpdateRequest(AdminUser user, Entity entity, PersistencePackage persistencePackage) {
        return new GlobalValidationResult(false);
    }

    @Override
    public GlobalValidationResult validateRemoveRequest(AdminUser user, Entity entity, PersistencePackage persistencePackage) {
        return new GlobalValidationResult(true);
    }

}
