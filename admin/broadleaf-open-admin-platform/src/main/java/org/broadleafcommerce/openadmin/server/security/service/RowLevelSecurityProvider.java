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

import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.CriteriaTranslatorEventHandler;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.CriteriaTranslatorImpl;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FilterMapping;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.GlobalValidationResult;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.PropertyValidator;
import org.broadleafcommerce.openadmin.web.form.entity.DefaultEntityFormActions;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.service.FormBuilderServiceImpl;

import java.io.Serializable;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * <p>
 * A component that can apply row-level security to the admin
 * 
 * <p>
 * Implementations of this class should extend from the {@link AbstractRowLevelSecurityProvider}
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link AbstractRowLevelSecurityProvider}
 * @see {@link RowLevelSecurityService}
 */
public interface RowLevelSecurityProvider {

    /**
     * <p>
     * Used to further restrict a result set in the admin for a particular admin user. This can be done by adding additional
     * {@link Predicate}s to the given list of <b>restrictions</b>. You can also attach additional sorting from the given
     * list of <b>sorts</b>.
     * 
     * <p>
     * You should not attach any of these {@link Predicate}s to the given <b>criteria</b>, you should instead modify the
     * given lists. These lists will be automatically attached to the <b>criteria</b> after execution.
     * 
     * <p>
     * Existing {@link Predicate}s and sorts will already be added into the given <b>restrictions</b> and <b>sorts</b> lists.
     * 
     * <p>
     * If you are filtering on a property that exists only in a subclass of an OOB framework entity (like if you extended
     * ProductImpl and you need to add a restriction targeting MyProductImpl) then you need to override
     * {@link #getFetchRestrictionRoot(AdminUser, Class, List)} to return the class that you are adding criteria to.
     * Otherwise, the <b>entityRoot</b> will be wrong and if you try to get a {@link Path} from it then you will run into
     * {@link IllegalArgumentException}s.
     * 
     * <p>
     * This method is executed <i>prior</i> to any {@link CriteriaTranslatorEventHandler}.
     * 
     * @param currentUser the currently logged in {@link AdminUser}
     * @param ceilingEntity the entity currently being queried from
     * @param restrictions the restrictions that will be applied to the <b>criteria</b> but have not been yet. Additional
     * {@link Predicate}s to further filter the query should be added to this list
     * @param sorts the sorts that will be applied to the <b>criteria</b>. Additional sorts should be added to this list
     * @param entityRoot the JPA root for <b>ceilingEntity</b>
     * @param criteria the criteria that will be executed. No {@link Predicate}s or {@link Order}s have been applied
     * to this criteria, and nor should they be. All modifications should instead be to the given <b>restrictions</b> and/or
     * <b>sorts</b>
     * @param criteriaBuilder used to create additional {@link Predicate}s or {@link Order}s to add to <b>restrictions</b>
     * and/or <b>sorts</b>
     * @see {@link #getFetchRestrictionRoot(Class, List)}
     * @see {@link CriteriaTranslatorImpl#addRestrictions}
     */
    public void addFetchRestrictions(AdminUser currentUser, String ceilingEntity, List<Predicate> restrictions, List<Order> sorts,
            Root entityRoot,
            CriteriaQuery criteria,
            CriteriaBuilder criteriaBuilder);
    
    /**
     * <p>
     * Contributes to {@link Root} determination for {@link #addFetchRestrictions(AdminUser, String, List, List, Root, CriteriaQuery, CriteriaBuilder)}.
     * Normally, the query {@link Root} is determined in the admin via the given <b>filterMappings</b>. Since row security deals with
     * a {@link CriteriaBuilder} directly, if you want to be able to target subclasses then a new {@link Root} must be
     * established for that specific subclass.
     * 
     * <p>
     * Note that depending on how you have your filters in the admin frontend (the list grids) set up, you might have to take
     * into account the given <b>filterMappings</b>. The admin will not be able to find a correct root if there is an active
     * filter set on a sibling class that you are attempting to also add more criteria to. For instance, if a class hierarchy
     * exists for A -> B and also A -> C, if there is an active {@link FilterMapping} for a property from B and you attempt
     * to add a fetch restriction on a property from C that will not work.
     * 
     * <p>
     * It is acceptable to return null from this method if {@link #addFetchRestrictions(AdminUser, String, List, List, Root, CriteriaQuery, CriteriaBuilder)}
     * does not rely on any properties from a child class. 
     * 
     * @param ceilingEntity the entity being queried for
     * @param filterMappings the existing filters passed from the admin frontend
     * 
     * @return the root class that is going to be used for {@link #addFetchRestrictions(AdminUser, String, List, List, Root, CriteriaQuery, CriteriaBuilder)}
     * or <b>null</b> if no specific root needs to be used
     * @see {@link CriteriaTranslatorImpl#determineRoot}
     * @see {@link FilterMapping#getInheritedFromClass()}
     */
    public Class<Serializable> getFetchRestrictionRoot(AdminUser currentUser, Class<Serializable> ceilingEntity, List<FilterMapping> filterMappings);
    
    /**
     * <p>
     * Hook to determine if the given <b>entity</b> can be updated or not. This is used to drive the form displayed in the
     * admin frontend to remove modifier actions and set the entire {@link EntityForm} as readonly.
     * 
     * <p>
     * If the entity cannot be updated, then by default it can also not be removed. You can change this by explicitly
     * overriding {@link #canRemove(AdminUser, Entity)}
     * 
     * @param currentUser the currently logged in {@link AdminUser}
     * @param entity the {@link Entity} DTO that is attempting to be updated
     * @return <b>true</b> if the given <b>entity</b> can be updated, <b>false</b> otherwise
     * @see {@link FormBuilderServiceImpl#setReadOnlyState}
     */
    public boolean canUpdate(AdminUser currentUser, Entity entity);
    
    /**
     * <p>
     * Hook to determine if the given <b>entity</b> can be deleted by a user. This is used to drive the {@link DefaultEntityFormActions#DELETE}
     * button from appearing on the admin frontend.
     * 
     * <p>
     * You might consider tying the remove to {@link #canUpdate(AdminUser, Entity)} and explicitly invoking that action yourself.
     * 
     * @param currentUser the currently logged in {@link AdminUser}
     * @param entity
     * @return <b>true</b> if the given <b>entity</b> can be deleted, <b>false</b> otherwise
     * @see {@link FormBuilderServiceImpl#addDeleteActionIfAllowed}
     */
    public boolean canRemove(AdminUser currentUser, Entity entity);
    
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
     * @param currentUser the currently logged in {@link AdminUser}
     * @param entity the DTO representation that is attempting to be deleted. Comes from {@link PersistencePackage#getEntity()}
     * @param persistencePackage the full persiste
     * @return a {@link GlobalValidationResult} with {@link GlobalValidationResult#isValid()} set to denote if the given
     * <b>entity</b> failed row-level security validation or not.
     */
    public GlobalValidationResult validateUpdateRequest(AdminUser currentUser, Entity entity, PersistencePackage persistencePackage);
    
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
     * @param currentUser the currently logged in {@link AdminUser}
     * @param entity the DTO representation that is attempting to be deleted. Comes from {@link PersistencePackage#getEntity()}
     * @param persistencePackage the full request sent from the frontend through the admin pipeline
     * @return a {@link GlobalValidationResult} with {@link GlobalValidationResult#isValid()} set to denote if the given
     * <b>entity</b> failed row-level security validation or not.
     */
    public GlobalValidationResult validateRemoveRequest(AdminUser currentUser, Entity entity, PersistencePackage persistencePackage);

}
