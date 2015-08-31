/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.core.search.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.SearchFacet;
import org.broadleafcommerce.core.search.domain.SearchFacetImpl;
import org.broadleafcommerce.core.search.domain.SearchField;
import org.broadleafcommerce.core.search.domain.SearchFieldImpl;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * @author Nick Crum (ncrum)
 */
@Repository("blSearchFieldDao")
public class SearchFieldDaoImpl implements SearchFieldDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public SearchField readSearchFieldForField(Field field) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<SearchField> criteria = builder.createQuery(SearchField.class);

        Root<SearchFieldImpl> search = criteria.from(SearchFieldImpl.class);

        criteria.select(search);
        criteria.where(
                builder.equal(search.join("field").get("id").as(Long.class), field.getId())
        );

        TypedQuery<SearchField> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
