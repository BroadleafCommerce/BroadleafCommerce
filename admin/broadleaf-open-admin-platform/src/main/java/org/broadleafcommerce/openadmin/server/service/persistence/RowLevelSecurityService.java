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
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.CriteriaTranslatorEventHandler;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.CriteriaTranslatorImpl;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.GlobalValidationResult;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.PropertyValidator;
import org.broadleafcommerce.openadmin.web.form.entity.DefaultEntityFormActions;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.service.FormBuilderServiceImpl;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * <p>
 * Provides row-level security to the various CRUD operations in the admin
 * 
 * <p>
 * This security service can be extended by the use of {@link RowLevelSecurityProviders}, of which this service has a list.
 * To add additional providers, add this to an applicationContext merged into the admin application:
 * 
 * {@code
 *  <bean id="blCustomRowSecurityProviders" class="org.springframework.beans.factory.config.ListFactoryBean" >
 *       <property name="sourceList">
 *          <list>
 *              <ref bean="customProvider" />
 *          </list>
 *      </property>
 *  </bean>
 *  <bean class="org.broadleafcommerce.common.extensibility.context.merge.LateStageMergeBeanPostProcessor">
 *      <property name="collectionRef" value="blCustomRowSecurityProviders" />
 *      <property name="targetRef" value="blRowLevelSecurityProviders" />
 *  </bean>
 * }
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @author Brian Polster (bpolster)
 */
public interface RowLevelSecurityService {

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
     * This method is executed <i>prior</i> to any {@link CriteriaTranslatorEventHandler}.
     * 
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
     * @see {@link CriteriaTranslatorImpl#addRestrictions}
     */
    public void addFetchRestrictions(AdminUser user, String ceilingEntity, List<Predicate> restrictions, List<Order> sorts,
            Root entityRoot,
            CriteriaQuery criteria,
            CriteriaBuilder criteriaBuilder);
    
    /**
     * <p>
     * Hook to determine if the given <b>entity</b> can be updated or not. This is used to drive the form displayed in the
     * admin frontend to remove modifier actions and set the entire {@link EntityForm} as readonly.
     * 
     * <p>
     * If the entity cannot be updated, then by default it can also not be removed. You can change this by explicitly
     * overriding {@link #canRemove(AdminUser, Entity)}
     * 
     * @param entity the {@link Entity} DTO that is attempting to be updated
     * @return <b>true</b> if the given <b>entity</b> can be updated, <b>false</b> otherwise
     * @see {@link FormBuilderServiceImpl#setReadOnlyState}
     */
    public boolean canUpdate(AdminUser user, Entity entity);
    
    /**
     * <p>
     * Hook to determine if the given <b>entity</b> can be deleted by a user. This is used to drive the {@link DefaultEntityFormActions#DELETE}
     * button from appearing on the admin frontend.
     * 
     * <p>
     * You might consider tying the remove to {@link #canUpdate(AdminUser, Entity)} and explicitly invoking that action yourself.
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
    
    /**
     * Gets all of the registered providers
     * @return the providers configured for this service
     */
    public List<RowLevelSecurityProvider> getProviders();
}
