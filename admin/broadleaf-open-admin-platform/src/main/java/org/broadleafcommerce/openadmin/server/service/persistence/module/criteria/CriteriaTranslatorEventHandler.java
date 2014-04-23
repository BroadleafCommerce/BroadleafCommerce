/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.server.service.persistence.module.criteria;

import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.FieldPersistenceProvider;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Event handler for adding restrictions to criteria created for admin fetch requests
 *
 * @author Jeff Fischer
 */
public interface CriteriaTranslatorEventHandler {

    /**
     * Allows the ability to attach additional criteria to the given <b>criteria</b> that has already been created by the
     * given <b>filterMappings</b>. Since translation has already occurred from the <b>filterMappings</b> into the given
     * <b>criteria</b>, implementers should attach additiona criteria there.
     * 
     * @param ceilingEntity the entity currently being fetched
     * @param filterMappings the DTO of filters harvested from {@link FieldPersistenceProvider}s
     * @param criteriaBuilder used for adding additional restrictions
     * @param original the Hibernate root from which restriction paths start from
     * @param restrictions existing list of restrictions already added to <b>criteria</b>
     * @param sorts list of sorts already applied to <b>criteria>
     * @param criteria what additional restrictions should be added to. This represents the query after all <b>filterMappings</b>,
     * <b>restrictions</b> and <b>sorts</b> have already been applied
     */
    void addRestrictions(String ceilingEntity, List<FilterMapping> filterMappings, CriteriaBuilder criteriaBuilder,
                         Root original, List<Predicate> restrictions, List<Order> sorts, CriteriaQuery criteria);

}
