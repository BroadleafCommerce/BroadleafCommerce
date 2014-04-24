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
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.CriteriaTranslatorImpl;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FilterMapping;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.GlobalValidationResult;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.PropertyValidator;
import org.broadleafcommerce.openadmin.web.form.entity.DefaultEntityFormActions;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.service.FormBuilderServiceImpl;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Provides row-level security to the various CRUD operations in the admin
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @author Brian Polster (bpolster)
 */
public interface RowLevelSecurityService {

    /**
     * <p>
     * Used to further restrict a result set in the admin
     * 
     * <p>
     * Existing {@link Predicate} that have already been applied can be retrieved with {@link CriteriaQuery#getRestriction()}
     * and existing sorts that have already been applied can be retrieved with {@link CriteriaQuery#getOrderList()}
     * 
     * @param ceilingEntity the entity currently being queried from
     * @param entityRoot the JPA root for <b>ceilingEntity</b>
     * @param criteria the built and populated JPA critieria with all {@link FilterMapping}s and 
     * @param criteriaBuilder used to append additional restrictions to the given <b>criteria</b>
     * @see {@link CriteriaTranslatorImpl#addRestrictions}
     */
    public void addFetchRestrictions(AdminUser user, String ceilingEntity, Root entityRoot, CriteriaQuery criteria, CriteriaBuilder criteriaBuilder);
    
    /**
     * Hook to determine if the given <b>entity</b> can be updated or not. This is used to drive the form displayed in the
     * admin frontend to remove modifier actions and set the entire {@link EntityForm} as readonly.
     * 
     * @param entity the {@link Entity} DTO that is attempting to be updated
     * @return <b>true</b> if the given <b>entity</b> can be updated, <b>false</b> otherwise
     * @see {@link FormBuilderServiceImpl#setReadOnlyState}
     */
    public boolean canUpdate(AdminUser user, Entity entity);
    
    /**
     * Hook to determine if the given <b>entity</b> can be updated or not. This is used to drive the {@link DefaultEntityFormActions#DELETE}
     * button from appearing on the admin frontend.
     * 
     * @param entity
     * @return <b>true</b> if the given <b>entity</b> can be deleted, <b>false</b> otherwise
     * @see {@link FormBuilderServiceImpl#addDeleteActionIfAllowed}
     */
    public boolean canRemove(AdminUser user, Entity entity);
    
    /**
     * <p>
     * Validates whether a user has permissions to actually perform the update. The result of this method is a
     * validation result that indicates if something in the entire entity is in error. The message key from the resulting
     * {@link GlobalValidationResult} will be automatically added to the given <b>entity</b> {@link Entity#getGlobalValidationErrors()}.
     * 
     * <p>
     * If you would like to add individual property errors, you can do that with the given <b>entity</b> by using
     * {@link Entity#addValidationError(String, String)}. Even if you attach errors to specific properties you should still
     * return an appropriate {@link GlobalValidationResult}. In that case however, it might be more suitable to use a
     * {@link PropertyValidator} instead.
     * 
     * <p>
     * For convenience, this is usually a simple invocation to {@link #canUpdate(Entity)}. However, it might be that you want
     * to allow the user to see certain update fields but not allow the user to save certain fields for update.
     * 
     * @param entity the DTO representation that is attempting to be deleted. Comes from {@link PersistencePackage#getEntity()}
     * @param persistencePackage the full persiste
     * @return a {@link GlobalValidationResult} with {@link GlobalValidationResult#isValid()} set to denote if the given
     * <b>entity</b> failed row-level security validation or not.
     */
    public GlobalValidationResult validateUpdateRequest(AdminUser user, Entity entity, PersistencePackage persistencePackage);
    
    /**
     * <p>
     * Validates whether a user has permissions to actually perform the record deletion. The result of this method is a
     * validation result that indicates if something in the entire entity is in error. The message key from the resulting
     * {@link GlobalValidationResult} will be automatically added to the given <b>entity</b> {@link Entity#getGlobalValidationErrors()}.
     * 
     * <p>
     * If you would like to add individual property errors, you can do that with the given <b>entity</b> by using
     * {@link Entity#addValidationError(String, String)}. Even if you attach errors to specific properties you should still
     * return an appropriate {@link GlobalValidationResult}. In that case however, it might be more suitable to use a
     * {@link PropertyValidator} instead.
     * 
     * <p>
     * This is usually a simple invocation to {@link #canDelete(Entity)}.
     * 
     * @param entity the DTO representation that is attempting to be deleted. Comes from {@link PersistencePackage#getEntity()}
     * @param persistencePackage the full request sent from the frontend through the admin pipeline
     * @return a {@link GlobalValidationResult} with {@link GlobalValidationResult#isValid()} set to denote if the given
     * <b>entity</b> failed row-level security validation or not.
     */
    public GlobalValidationResult validateRemoveRequest(AdminUser user, Entity entity, PersistencePackage persistencePackage);
    
}
